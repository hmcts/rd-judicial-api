terraform {
  required_version = "~> 0.12"  # Terraform client version
  backend "azurerm" {}
  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
      version = "~> 2.25"
    }
    random = {
      source = "hashicorp/random"
    }
  }
}
