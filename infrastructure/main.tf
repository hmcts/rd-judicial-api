locals {
  preview_vault_name      = join("-", [var.raw_product, "aat"])
  non_preview_vault_name  = join("-", [var.raw_product, var.env])
  key_vault_name          = var.env == "preview" || var.env == "spreview" ? local.preview_vault_name : local.non_preview_vault_name

  s2s_rg_prefix               = "rpe-service-auth-provider"
  s2s_key_vault_name          = var.env == "preview" || var.env == "spreview" ? join("-", ["s2s", "aat"]) : join("-", ["s2s", var.env])
  s2s_vault_resource_group    = var.env == "preview" || var.env == "spreview" ? join("-", [local.s2s_rg_prefix, "aat"]) : join("-", [local.s2s_rg_prefix, var.env])

  #vm
  vm_count = 1
  vm_type = "windows"
  vm_size = "Standard_D2_v5"
  vm_availability_zones  = [1, 2]
  ipconfig_name = "IP_CONFIG"
  marketplace_product     = "WindowsServer"
  marketplace_sku       = "2016-Datacenter"
  marketplace_publisher = "MicrosoftWindowsServer"
  vm_version            = "latest"
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

data "azurerm_key_vault" "rd_key_vault" {
  name                = local.key_vault_name
  resource_group_name = local.key_vault_name
}

resource "azurerm_key_vault_secret" "judicial_s2s_secret" {
  name          = "judicial-api-s2s-secret"
  value         = data.azurerm_key_vault_secret.s2s_secret.value
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name          = join("-", [var.component, "POSTGRES-USER"])
  value         = module.db-judicial-ref-data.user_name
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name          = join("-", [var.component, "POSTGRES-PASS"])
  value         = module.db-judicial-ref-data.postgresql_password
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name          = join("-", [var.component, "POSTGRES-HOST"])
  value         = module.db-judicial-ref-data.host_name
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name          = join("-", [var.component, "POSTGRES-PORT"])
  value         = "5432"
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name          = join("-", [var.component, "POSTGRES-DATABASE"])
  value         = module.db-judicial-ref-data.postgresql_database
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "vm_password" {
  name = "vm-password"
  value = random_string.password.result
  key_vault_id = data.azurerm_key_vault.rd_key_vault.id
}
resource "random_string" "password" {
  length = 16
  special = true
  upper = true
  lower = true
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

module "vm-judicial-ref-data" {
  count                = local.vm_count
  source               = "git@github.com:hmcts/terraform-vm-module.git?ref=master"
  vm_type              = local.vm_type
  vm_name              = lower("RD-VM${count.index + 1}-${var.env}")
  vm_resource_group    = var.vm_resource_group
  vm_location          = var.location
  vm_size              = local.vm_size
  vm_admin_name        = "rd-admin${count.index + 1}"
  vm_admin_password    = azurerm_key_vault_secret.vm_password
  vm_availabilty_zones = local.vm_availability_zones[count.index]

  #Disk Encryption
  encrypt_ADE = true
  nic_name      = lower("RD-VM${count.index + 1}-nic-${var.env}")
  ipconfig_name = local.ipconfig_name
  vm_subnet_id = data.azurerm_subnet.aks-01.id
  #storage_image_reference
  vm_publisher_name = local.marketplace_publisher
  vm_offer          = local.marketplace_product
  vm_sku            = local.marketplace_sku
  vm_version        = local.vm_version
  #this is to mount the disks
  tags = var.common_tags
}

