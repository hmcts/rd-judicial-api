terraform {
  required_providers {
    azurerm = {
      # The "hashicorp" namespace is the new home for the HashiCorp-maintained
      # provider plugins.
      #
      # source is not required for the hashicorp/* namespace as a measure of
      # backward compatibility for commonly-used providers, but recommended for
      # explicitness.
      source  = "hashicorp/azurerm"
      version = "~> 2.12"
    }
    newrelic = {
      # source is required for providers in other namespaces, to avoid ambiguity.
      source  = "newrelic/newrelic"
      version = "~> 2.1.1"
    }
  }
}
