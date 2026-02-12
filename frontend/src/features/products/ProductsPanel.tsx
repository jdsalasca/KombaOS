import { useState } from "react";
import { useProducts } from "./useProducts";

export function ProductsPanel() {
  const { products, productsState, selectedProductId, setSelectedProductId, selectedProduct, actions } = useProducts();

  const [createProductName, setCreateProductName] = useState("");
  const [createProductDescription, setCreateProductDescription] = useState("");
  const [createProductPriceCents, setCreateProductPriceCents] = useState("");
  const [createProductCurrency, setCreateProductCurrency] = useState("COP");
  const [createProductActive, setCreateProductActive] = useState(true);
  const [createError, setCreateError] = useState("");
  const [updateError, setUpdateError] = useState("");

  function validateProductInput(input: { name: string; priceCents: string; currency: string }) {
    if (!input.name.trim().length) return "Ingresa el nombre del producto.";
    const parsed = Number(input.priceCents.trim());
    if (!Number.isFinite(parsed) || parsed < 0) return "El precio debe ser un número válido.";
    const currency = input.currency.trim();
    if (!/^[A-Za-z]{3}$/.test(currency)) return "La moneda debe tener 3 letras.";
    return "";
  }


  return (
    <div className="card panel">
      <div className="panel__header">
        <div>
          <h2 className="panel__title">Productos</h2>
          <p className="panel__subtitle">Administra precios y disponibilidad del catálogo.</p>
        </div>
        <span className="pill">{products.length} activos</span>
      </div>

      <div className="form">
        <div>
          <h3 className="panel__title">Crear producto</h3>
          <form
            onSubmit={async (e) => {
              e.preventDefault();
              const validation = validateProductInput({
                name: createProductName,
                priceCents: createProductPriceCents,
                currency: createProductCurrency,
              });
              if (validation) {
                setCreateError(validation);
                return;
              }
              setCreateError("");
              await actions.create({
                name: createProductName,
                description: createProductDescription,
                priceCents: createProductPriceCents,
                currency: createProductCurrency,
                active: createProductActive,
              });
              setCreateProductName("");
              setCreateProductDescription("");
              setCreateProductPriceCents("");
              setCreateProductCurrency("COP");
              setCreateProductActive(true);
            }}
            className="formRow"
          >
            <label className="field">
              <span className="field__label">Nombre</span>
              <input
                className="field__input"
                aria-label="Nombre producto"
                placeholder="Ej. Mesa de roble"
                value={createProductName}
                onChange={(e) => setCreateProductName(e.target.value)}
              />
            </label>
            <label className="field">
              <span className="field__label">Descripción</span>
              <input
                className="field__input"
                aria-label="Descripción producto"
                placeholder="Ej. Hecha a mano"
                value={createProductDescription}
                onChange={(e) => setCreateProductDescription(e.target.value)}
              />
            </label>
            <label className="field">
              <span className="field__label">Precio</span>
              <input
                className="field__input"
                aria-label="Precio (centavos)"
                inputMode="numeric"
                value={createProductPriceCents}
                onChange={(e) => setCreateProductPriceCents(e.target.value)}
              />
              <span className="field__hint">Valor en centavos.</span>
            </label>
            <label className="field">
              <span className="field__label">Moneda</span>
              <input
                className="field__input"
                aria-label="Moneda"
                placeholder="COP"
                value={createProductCurrency}
                onChange={(e) => setCreateProductCurrency(e.target.value)}
              />
            </label>
            <label className="field inline">
              <input
                aria-label="Producto activo"
                type="checkbox"
                checked={createProductActive}
                onChange={(e) => setCreateProductActive(e.target.checked)}
              />
              Producto activo
            </label>
            <button className="button button--primary" type="submit">
              Crear producto
            </button>
          </form>
          {createError ? <p className="field__error">{createError}</p> : null}
        </div>
      </div>

      {productsState.status === "loading" && <p className="muted">Cargando productos...</p>}
      {productsState.status === "error" && <p className="error">{productsState.message}</p>}
      {productsState.status === "loaded" && !products.length && <p className="muted">Aún no hay productos registrados.</p>}

      <ul className="list">
        {products.map((p) => (
          <li key={p.id} className="list__item">
            <button
              type="button"
              onClick={() => setSelectedProductId(p.id)}
              className={p.id === selectedProductId ? "listButton listButton--active" : "listButton"}
            >
              <span className="listButton__title">{p.name}</span>
              <span className="listButton__meta">
                {p.currency} {p.priceCents / 100} · {p.active ? "Activo" : "Inactivo"}
              </span>
            </button>
          </li>
        ))}
      </ul>

      <div>
        <h3 className="panel__title">Editar producto</h3>
        <form
          key={selectedProductId ?? "none"}
          onSubmit={async (e) => {
            e.preventDefault();
            if (!selectedProductId) return;
            const form = e.currentTarget as HTMLFormElement;
            const data = new FormData(form);
            const activeInput = form.elements.namedItem("active") as HTMLInputElement | null;
            const input = {
              name: String(data.get("name") ?? ""),
              description: String(data.get("description") ?? ""),
              priceCents: String(data.get("priceCents") ?? ""),
              currency: String(data.get("currency") ?? ""),
              active: activeInput?.checked ?? false,
            };
            const validation = validateProductInput({
              name: input.name,
              priceCents: input.priceCents,
              currency: input.currency,
            });
            if (validation) {
              setUpdateError(validation);
              return;
            }
            setUpdateError("");
            await actions.update(selectedProductId, input);
          }}
          className="formCol"
        >
          <div className="formRow">
            <label className="field">
              <span className="field__label">Nombre</span>
              <input
                className="field__input"
                aria-label="Editar nombre producto"
                placeholder="Nombre"
                name="name"
                defaultValue={selectedProduct?.name ?? ""}
                disabled={!selectedProductId}
              />
            </label>
            <label className="field">
              <span className="field__label">Descripción</span>
              <input
                className="field__input"
                aria-label="Editar descripción producto"
                placeholder="Descripción"
                name="description"
                defaultValue={selectedProduct?.description ?? ""}
                disabled={!selectedProductId}
              />
            </label>
            <label className="field">
              <span className="field__label">Precio</span>
              <input
                className="field__input"
                aria-label="Editar precio (centavos)"
                inputMode="numeric"
                name="priceCents"
                defaultValue={selectedProduct ? String(selectedProduct.priceCents) : ""}
                disabled={!selectedProductId}
              />
            </label>
            <label className="field">
              <span className="field__label">Moneda</span>
              <input
                className="field__input"
                aria-label="Editar moneda"
                name="currency"
                defaultValue={selectedProduct?.currency ?? "COP"}
                disabled={!selectedProductId}
              />
            </label>
            <label className="field inline">
              <input
                aria-label="Editar producto activo"
                type="checkbox"
                name="active"
                defaultChecked={selectedProduct?.active ?? false}
                disabled={!selectedProductId}
              />
              Activo
            </label>
          </div>
          {updateError ? <p className="field__error">{updateError}</p> : null}
          <div className="formRow">
            <button className="button button--primary" type="submit" disabled={!selectedProductId}>
              Guardar cambios
            </button>
            <button
              className="button button--danger"
              type="button"
              onClick={async () => {
                if (!selectedProductId) return;
                await actions.remove(selectedProductId);
                setSelectedProductId(null);
              }}
              disabled={!selectedProductId}
            >
              Eliminar
            </button>
          </div>
        </form>
      </div>

      <p className="muted">Seleccionado: {selectedProduct ? selectedProduct.name : "—"}</p>
    </div>
  );
}
