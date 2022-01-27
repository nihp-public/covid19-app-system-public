locals {
  identifier_prefix = "${terraform.workspace}-${var.name}"
}

resource "aws_codebuild_source_credential" "this" {
  auth_type   = "PERSONAL_ACCESS_TOKEN"
  server_type = "GITHUB"
  token       = var.github_api_token
}

data "template_file" "buildspec" {
  template = file(var.pipeline_definition_file)
}

resource "aws_codebuild_project" "this" {
  name           = var.name
  description    = "Deployment pipeline"
  build_timeout  = "30"
  service_role   = var.service_role
  source_version = "master"

  tags = var.tags

  artifacts {
    type                   = "S3"
    location               = var.artifacts_bucket_name
    packaging              = "ZIP"
    override_artifact_name = true
  }

  environment {
    compute_type                = "BUILD_GENERAL1_MEDIUM"
    image                       = var.container
    type                        = "LINUX_CONTAINER"
    image_pull_credentials_type = var.image_pull_credentials_type
    privileged_mode             = var.privileged_mode

    environment_variable {
      name  = "ACCOUNT"
      value = var.account
    }
    environment_variable {
      name  = "TARGET_ENVIRONMENT"
      value = "demo"
    }
  }

  logs_config {
    cloudwatch_logs {
      group_name  = "${local.identifier_prefix}-codebuild"
      stream_name = "${local.identifier_prefix}-codebuild"
    }
  }

  source {
    buildspec = data.template_file.buildspec.rendered
    type      = "GITHUB"
    location  = var.repository

    git_submodules_config {
      fetch_submodules = false
    }
    auth {
      type     = "OAUTH"
      resource = aws_codebuild_source_credential.this.arn
    }
  }
}


resource "aws_codebuild_webhook" "this" {
  depends_on = [
    aws_codebuild_project.this
  ]
  project_name = var.name

  filter_group {
    filter {
      type    = "EVENT"
      pattern = "PULL_REQUEST_CREATED"
    }
    filter {
      type    = "FILE_PATH"
      pattern = var.file_path
    }
  }

  filter_group {
    filter {
      type    = "EVENT"
      pattern = "PULL_REQUEST_UPDATED"
    }
    filter {
      type    = "FILE_PATH"
      pattern = var.file_path
    }
  }

  filter_group {
    filter {
      type    = "EVENT"
      pattern = "PUSH"
    }
    filter {
      type    = "HEAD_REF"
      pattern = "pr/"
    }
    filter {
      type    = "FILE_PATH"
      pattern = var.file_path
    }
  }
}
