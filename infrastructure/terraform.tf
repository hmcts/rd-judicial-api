provider "azurerm" {
  version = "2.20.0"
  features {}
}

terraform {
  backend “azurerm” {}
}