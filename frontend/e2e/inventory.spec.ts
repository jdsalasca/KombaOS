import { expect, test } from "@playwright/test";

test("inventario: material, umbral, alertas y movimiento", async ({ page }) => {
  await page.goto("/");

  const materialesPanel = page
    .getByRole("heading", { name: "Materiales", exact: true })
    .locator('xpath=ancestor::*[contains(@class,"panel")][1]');

  await materialesPanel.getByLabel("Nombre material", { exact: true }).fill("Lana E2E");
  await materialesPanel.getByLabel("Unidad material", { exact: true }).fill("kg");
  await materialesPanel.getByRole("button", { name: "Crear", exact: true }).click();

  const materialButton = materialesPanel.getByRole("button", { name: /lana e2e/i });
  await expect(materialButton).toBeVisible();
  await materialButton.click();

  await page.getByLabel("Stock mínimo").fill("10");
  await page.getByRole("button", { name: "Guardar umbral" }).click();

  const qtyInput = page.getByLabel("Cantidad movimiento");
  await expect(page.getByText("BAJO")).toBeVisible();

  await qtyInput.fill("5");
  await expect(qtyInput).toHaveValue("5");
  await page.getByRole("button", { name: "Crear movimiento" }).click();

  await expect(page.getByText(/5\s+kg/)).toBeVisible();

  await qtyInput.fill("20");
  await expect(qtyInput).toHaveValue("20");
  await page.getByRole("button", { name: "Crear movimiento" }).click();

  await expect(page.getByText(/20\s+kg/)).toBeVisible();
  await expect(page.getByText("OK")).toBeVisible();
});

test("materiales: filtros por proveedor y certificación", async ({ page }) => {
  await page.goto("/");

  const materialesPanel = page
    .getByRole("heading", { name: "Materiales", exact: true })
    .locator('xpath=ancestor::*[contains(@class,"panel")][1]');

  await materialesPanel.getByLabel("Nombre material", { exact: true }).fill("Algodón filtro");
  await materialesPanel.getByLabel("Unidad material", { exact: true }).fill("kg");
  await materialesPanel.getByLabel("Proveedor material", { exact: true }).fill("Proveedor A");
  await materialesPanel.getByLabel("Material certificado", { exact: true }).check();
  await materialesPanel.getByRole("button", { name: "Crear", exact: true }).click();

  await materialesPanel.getByLabel("Nombre material", { exact: true }).fill("Lana filtro");
  await materialesPanel.getByLabel("Unidad material", { exact: true }).fill("kg");
  await materialesPanel.getByLabel("Proveedor material", { exact: true }).fill("Proveedor B");
  await materialesPanel.getByLabel("Material certificado", { exact: true }).uncheck();
  await materialesPanel.getByRole("button", { name: "Crear", exact: true }).click();

  await materialesPanel.getByLabel("Filtrar por proveedor").fill("Proveedor A");
  await materialesPanel.getByRole("button", { name: "Aplicar", exact: true }).click();
  await expect(materialesPanel.getByRole("button", { name: /algodón filtro/i })).toBeVisible();
  await expect(materialesPanel.getByRole("button", { name: /lana filtro/i })).toHaveCount(0);

  await materialesPanel.getByRole("button", { name: "Limpiar", exact: true }).click();
  await materialesPanel.getByLabel("Filtrar por certificación").selectOption("true");
  await materialesPanel.getByRole("button", { name: "Aplicar", exact: true }).click();
  await expect(materialesPanel.getByRole("button", { name: /algodón filtro/i })).toBeVisible();
  await expect(materialesPanel.getByRole("button", { name: /lana filtro/i })).toHaveCount(0);
});
