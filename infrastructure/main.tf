locals {
  preview_vault_name      = join("-", [var.raw_product, "aat"])
  non_preview_vault_name  = join("-", [var.raw_product, var.env])
  key_vault_name          = var.env == "preview" || var.env == "spreview" ? local.preview_vault_name : local.non_preview_vault_name

  s2s_rg_prefix               = "rpe-service-auth-provider"
  s2s_key_vault_name          = var.env == "preview" || var.env == "spreview" ? join("-", ["s2s", "aat"]) : join("-", ["s2s", var.env])
  s2s_vault_resource_group    = var.env == "preview" || var.env == "spreview" ? join("-", [local.s2s_rg_prefix, "aat"]) : join("-", [local.s2s_rg_prefix, var.env])
}

data "azurerm_key_vault" "rd_key_vault" {
  name                = local.key_vault_name
  resource_group_name = local.key_vault_name
}

data "azurerm_key_vault" "s2s_key_vault" {
  name                = local.s2s_key_vault_name
  resource_group_name = local.s2s_vault_resource_group
}

data "azurerm_key_vault_secret" "s2s_secret" {
  name          = "microservicekey-rd-judicial-api"
  key_vault_id  = data.azurerm_key_vault.s2s_key_vault.id
}

resource "azurerm_key_vault_secret" "judicial_s2s_secret" {
  name          = "judicial-api-s2s-secret"
  value         = data.azurerm_key_vault_secret.s2s_secret.value
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name          = join("-", [var.component, "POSTGRES-PASS"])
  value         = module.db-judicial-ref-data.postgresql_password
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

module "db-judicial-ref-data" {
  source              = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product             = var.product
  component           = var.component
  name                = join("-", [var.product, var.component, "postgres-db"])
  location            = var.location
  subscription        = var.subscription
  env                 = var.env
  postgresql_user     = "dbjuddata"
  database_name       = "dbjuddata"
  common_tags         = var.common_tags
  postgresql_version  = var.postgresql_version
}