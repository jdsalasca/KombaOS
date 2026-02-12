import { expect, test } from "@playwright/test";

test("productos: crear producto y verlo en la lista", async ({ page }) => {
  await page.goto("/");

  const productosPanel = page
    .getByRole("heading", { name: "Productos", exact: true })
    .locator('xpath=ancestor::*[contains(@class,"panel")][1]');

  await productosPanel.getByLabel("Nombre producto", { exact: true }).fill("Ruana E2E");
  await productosPanel.getByLabel("Descripción producto", { exact: true }).fill("Ruana de lana (E2E)");
  await productosPanel.getByLabel("Precio (centavos)", { exact: true }).fill("15000000");
  await productosPanel.getByLabel("Moneda", { exact: true }).fill("COP");
  await productosPanel.getByRole("button", { name: "Crear", exact: true }).click();

  await expect(productosPanel.getByText("Ruana E2E", { exact: true })).toBeVisible();
});
