import { expect, test } from "@playwright/test";

test("inventario: material, umbral, alertas y movimiento", async ({ page }) => {
  await page.goto("/");

  await page.getByLabel("Nombre material").fill("Lana E2E");
  await page.getByLabel("Unidad material").fill("kg");
  await page.getByRole("button", { name: "Crear", exact: true }).click();

  const materialButton = page.getByRole("button", { name: /lana e2e/i });
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
