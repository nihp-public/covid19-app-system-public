require_relative "target"
require_relative "versions"
require "json"

module NHSx
  # Helpers for generating configurations and test input data
  module Generate
    include NHSx::TargetEnvironment
    include Gaudi::Utilities
    # Uses terraform output to query the target environment and generate
    # the test configuration file that drives the sanity tests
    #
    # The configuration is generated under out/gen/config
    #
    # Returns the full path to the generated configuration file
    def generate_test_config(environment_name, account_name, system_config)
      test_config_file = File.join(system_config.out, "gen/config", "test_config_#{environment_name}.json")

      target_config = target_environment_configuration(environment_name, account_name, system_config)
      write_file(test_config_file, JSON.dump(target_config))
      return test_config_file
    end

    def generate_analytics_test_config(environment_name, account_name, system_config)
      test_config_file = File.join(system_config.out, "gen/config/analytics", "test_config_#{environment_name}.json")

      target_config = analytics_target_environment_configuration(environment_name, account_name, system_config)
      write_file(test_config_file, JSON.dump(target_config))
      return test_config_file
    end

    def generate_ssh_keypair(system_config)
      key_file = File.join(system_config.out, "ssh", "ephemeral_deploy_id_rsa")
      file key_file do
        mkdir_p(File.dirname(key_file), :verbose => false)
        cmdline = "ssh-keygen -t rsa -C \"ephemeral-deploy@nhsx.nhs.uk\" -f #{key_file}"
        run_command("Create ephemeral SSH keypair", cmdline, system_config)
      end
      Rake::Task[key_file].invoke
      return key_file
    end

    def generate_local_messages(mapping_filepath, metadata_filepath, system_config)
      mapping = JSON.parse(File.read(mapping_filepath))
      metadata = JSON.parse(File.read(metadata_filepath))
      check_if_las_exists(mapping["las"].keys,system_config)
      metadata["messages"] = filter_unused_messages_from_metadata(mapping["las"], metadata["messages"])
      metadata["messages"].each { |_, msg| msg["updated"] = Time.now.utc.iso8601 }
      mapping_metadata = { "las" => mapping["las"], "messages" => metadata["messages"] }
      output_file = "#{system_config.out}/local-messages/local-messages.json"
      write_file(output_file, JSON.pretty_generate(mapping_metadata))
    end

    def filter_unused_messages_from_metadata(la_mapping, messages_metadata)
      used_messages = la_mapping.values.each_with_object([]) do |mapped_msgs, used_msgs|
        used_msgs.append(*mapped_msgs)
      end.uniq
      messages_metadata.select { |k, _| used_messages.include?(k) }
    end

    def check_if_las_exists(las,system_config)
      list_of_las = JSON.parse(File.read("#{system_config.base}/tools/lokalise/localAuthorities.json"))
      las_not_exists = las - list_of_las.keys
      raise GaudiError, "The following are not valid local authority IDs in the LAD20CD format #{las_not_exists}" unless las_not_exists.empty?

    end

    def generate_from_template(dest:, template:, columns:)
        quicksight_glue_file = File.join($configuration.base, dest)
        quicksight_glue_template_file = File.join($configuration.base, template)
        write_file(
          quicksight_glue_file,
          from_template(quicksight_glue_template_file, { :columns => columns, :template_file => template })
        )
    end
  end
end
