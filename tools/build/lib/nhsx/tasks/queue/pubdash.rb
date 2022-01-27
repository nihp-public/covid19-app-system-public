namespace :queue do
  namespace :deploy do
    namespace :pubdash do
      NHSx::TargetEnvironment::PUBDASH_TARGET_ENVIRONMENTS.each do |account, tgt_envs|
        tgt_envs.each do |tgt_env|
          desc "Queue a public dashboard deployment to the #{tgt_env} target environment in CodeBuild"
          task :"#{tgt_env}" => [:"login:#{account}"] do
            include NHSx::Queue
            branch_name = branch_to_queue(tgt_env, $configuration)
            build_parameters = {
              "project_name" => "deploy-pubdash-#{tgt_env}",
              "source_version" => branch_name,
              "target_environment" => tgt_env,
              "account" => account
            }
            build_info = queue(build_parameters, $configuration)
            if $configuration.print_logs
              pipe_logs(build_info)
              puts "Download the full logs with \n\trake download:codebuild:#{account} JOB_ID=#{build_info.build_id}"
            else
              puts "Job queued. Download logs with \n\trake download:codebuild:#{account} JOB_ID=#{build_info.build_id}"
            end
          end
        end
      end
    end
  end
  namespace :release do
    desc "Queue a release to the staging target environment in CodeBuild"
    task :"pubdash:staging" => [:"login:staging"] do
      include NHSx::Queue
      branch_name = branch_to_queue("staging", $configuration)
      build_parameters = {
        "project_name" => "release-pubdash-staging",
        "source_version" => branch_name,
        "target_environment" => "staging",
        "account" => "staging"
      }
      build_info = queue(build_parameters, $configuration)
      if $configuration.print_logs
        pipe_logs(build_info)
        puts "Download the full logs with \n\trake download:codebuild:staging JOB_ID=#{build_info.build_id}"
      else
        puts "Job queued. Download logs with \n\trake download:codebuild:staging JOB_ID=#{build_info.build_id}"
      end
    end
    desc "Queue a release to the prod target environment in CodeBuild"
    task :"pubdash:prod" => [:"login:prod"] do
      include NHSx::Queue
      version_metadata = subsystem_version_metadata("pubdash", $configuration)
      release_version = $configuration.release_version(version_metadata)
      build_parameters = {
            "project_name" => "release-pubdash-prod",
            "source_version" => "te-staging-pubdash",
            "target_environment" => "prod",
            "account" => "prod",
            "release_version" => release_version
          }
      build_info = queue(build_parameters, $configuration)
      if $configuration.print_logs
        pipe_logs(build_info)
        puts "Download the full logs with \n\trake download:codebuild:prod JOB_ID=#{build_info.build_id}"
      else
        puts "Job queued. Download logs with \n\trake download:codebuild:prod JOB_ID=#{build_info.build_id}"
      end
    end
    desc "Queue a release to the aa-prod target environment in CodeBuild"
    task :"pubdash:aa-prod" => [:"login:aa-prod"] do
      include NHSx::Queue
      version_metadata = subsystem_version_metadata("pubdash", $configuration)
      release_version = $configuration.release_version(version_metadata)
      build_parameters = {
        "project_name" => "release-pubdash-aa-prod",
        "source_version" => "te-aa-staging-pubdash",
        "target_environment" => "aa-prod",
        "account" => "aa-prod",
        "release_version" => release_version
      }
      build_info = queue(build_parameters, $configuration)
      if $configuration.print_logs
        pipe_logs(build_info)
        puts "Download the full logs with \n\trake download:codebuild:prod JOB_ID=#{build_info.build_id}"
      else
        puts "Job queued. Download logs with \n\trake download:codebuild:prod JOB_ID=#{build_info.build_id}"
      end
    end
  end
end
