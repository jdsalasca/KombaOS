import { expect, test } from "@playwright/test";

test("inventario: material, umbral, alertas y movimiento", async ({ page }) => {
  await page.goto("/");

  await page.getByLabel("Nombre", { exact: true }).fill("E2E");
  await page.getByRole("button", { name: "Entrar al sistema", exact: true }).click();

  const materialesPanel = page.locator("section#materiales .panel");
  await materialesPanel.scrollIntoViewIfNeeded();

  await materialesPanel.getByLabel("Nombre material", { exact: true }).fill("Lana E2E");
  await materialesPanel.getByLabel("Unidad material", { exact: true }).fill("kg");
  await materialesPanel.getByRole("button", { name: "Crear material", exact: true }).click();

  const materialButton = materialesPanel.getByRole("button", { name: /lana e2e/i });
  await expect(materialButton).toBeVisible();
  await materialButton.click();

  const inventarioPanel = page.locator("section#inventario .panel");
  await inventarioPanel.scrollIntoViewIfNeeded();
  await inventarioPanel.getByLabel("Stock mínimo", { exact: true }).fill("10");
  await inventarioPanel.getByRole("button", { name: "Guardar umbral", exact: true }).click();

  const qtyInput = inventarioPanel.getByLabel("Cantidad movimiento", { exact: true });
  await expect(inventarioPanel.getByText("BAJO", { exact: true })).toBeVisible();

  await qtyInput.fill("5");
  await expect(qtyInput).toHaveValue("5");
  await inventarioPanel.getByRole("button", { name: "Registrar", exact: true }).click();

  await expect(inventarioPanel.getByText(/5\s+kg/)).toBeVisible();

  await qtyInput.fill("20");
  await expect(qtyInput).toHaveValue("20");
  await inventarioPanel.getByRole("button", { name: "Registrar", exact: true }).click();

  await expect(inventarioPanel.getByText(/20\s+kg/)).toBeVisible();
  await expect(inventarioPanel.getByText("OK", { exact: true })).toBeVisible();
});

test("materiales: filtros por proveedor y certificación", async ({ page }) => {
  await page.goto("/");

  await page.getByLabel("Nombre", { exact: true }).fill("E2E");
  await page.getByRole("button", { name: "Entrar al sistema", exact: true }).click();

  const materialesPanel = page.locator("section#materiales .panel");
  await materialesPanel.scrollIntoViewIfNeeded();

  await materialesPanel.getByLabel("Nombre material", { exact: true }).fill("Algodón filtro");
  await materialesPanel.getByLabel("Unidad material", { exact: true }).fill("kg");
  await materialesPanel.getByLabel("Proveedor material", { exact: true }).fill("Proveedor A");
  await materialesPanel.getByLabel("Material certificado", { exact: true }).check();
  await materialesPanel.getByRole("button", { name: "Crear material", exact: true }).click();

  await materialesPanel.getByLabel("Nombre material", { exact: true }).fill("Lana filtro");
  await materialesPanel.getByLabel("Unidad material", { exact: true }).fill("kg");
  await materialesPanel.getByLabel("Proveedor material", { exact: true }).fill("Proveedor B");
  await materialesPanel.getByLabel("Material certificado", { exact: true }).uncheck();
  await materialesPanel.getByRole("button", { name: "Crear material", exact: true }).click();

  await materialesPanel.getByLabel("Filtrar por proveedor").fill("Proveedor A");
  await materialesPanel.getByRole("button", { name: "Aplicar filtros", exact: true }).click();
  await expect(materialesPanel.getByRole("button", { name: /algodón filtro/i })).toBeVisible();
  await expect(materialesPanel.getByRole("button", { name: /lana filtro/i })).toHaveCount(0);

  await materialesPanel.getByRole("button", { name: "Limpiar", exact: true }).click();
  await materialesPanel.getByLabel("Filtrar por certificación").selectOption("true");
  await materialesPanel.getByRole("button", { name: "Aplicar filtros", exact: true }).click();
  await expect(materialesPanel.getByRole("button", { name: /algodón filtro/i })).toBeVisible();
  await expect(materialesPanel.getByRole("button", { name: /lana filtro/i })).toHaveCount(0);
});
