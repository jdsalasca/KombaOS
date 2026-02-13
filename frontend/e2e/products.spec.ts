import { expect, test } from "@playwright/test";

test("productos: crear producto y verlo en la lista", async ({ page }) => {
  await page.goto("/");

  await page.getByLabel("Nombre", { exact: true }).fill("E2E");
  await page.getByRole("button", { name: "Entrar al sistema", exact: true }).click();

  const productosPanel = page.locator("section#productos .panel");
  await productosPanel.scrollIntoViewIfNeeded();

  await productosPanel.getByLabel("Nombre producto", { exact: true }).fill("Ruana E2E");
  await productosPanel.getByLabel("Descripción producto", { exact: true }).fill("Ruana de lana (E2E)");
  await productosPanel.getByLabel("Precio (COP)", { exact: true }).fill("150000");
  await productosPanel.getByRole("button", { name: "Crear producto", exact: true }).click();

  await expect(productosPanel.getByText("Ruana E2E", { exact: true })).toBeVisible();
});
