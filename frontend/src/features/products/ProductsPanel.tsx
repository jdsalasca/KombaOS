import { useState } from "react";
import { useProducts } from "./useProducts";

export function ProductsPanel() {
  const { products, productsState, selectedProductId, setSelectedProductId, selectedProduct, actions } = useProducts();

  const [createProductName, setCreateProductName] = useState("");
  const [createProductDescription, setCreateProductDescription] = useState("");
  const [createProductPriceCents, setCreateProductPriceCents] = useState("");
  const [createProductCurrency, setCreateProductCurrency] = useState("COP");
  const [createProductActive, setCreateProductActive] = useState(true);


  return (
    <div className="panel">
      <h2>Productos</h2>
      <form
        onSubmit={async (e) => {
          e.preventDefault();
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
        <input
          aria-label="Nombre producto"
          placeholder="Nombre"
          value={createProductName}
          onChange={(e) => setCreateProductName(e.target.value)}
        />
        <input
          aria-label="Descripción producto"
          placeholder="Descripción"
          value={createProductDescription}
          onChange={(e) => setCreateProductDescription(e.target.value)}
        />
        <input
          aria-label="Precio (centavos)"
          inputMode="numeric"
          value={createProductPriceCents}
          onChange={(e) => setCreateProductPriceCents(e.target.value)}
        />
        <input
          aria-label="Moneda"
          placeholder="COP"
          value={createProductCurrency}
          onChange={(e) => setCreateProductCurrency(e.target.value)}
        />
        <label className="inline">
          <input
            aria-label="Producto activo"
            type="checkbox"
            checked={createProductActive}
            onChange={(e) => setCreateProductActive(e.target.checked)}
          />
          Activo
        </label>
        <button type="submit">Crear</button>
      </form>

      {productsState.status === "loading" && <p className="muted">Cargando...</p>}
      {productsState.status === "error" && <p className="error">Error: {productsState.message}</p>}

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

      <h3>Editar producto</h3>
      <form
        key={selectedProductId ?? "none"}
        onSubmit={async (e) => {
          e.preventDefault();
          if (!selectedProductId) return;
          const form = e.currentTarget as HTMLFormElement;
          const data = new FormData(form);
          const activeInput = form.elements.namedItem("active") as HTMLInputElement | null;
          await actions.update(selectedProductId, {
            name: String(data.get("name") ?? ""),
            description: String(data.get("description") ?? ""),
            priceCents: String(data.get("priceCents") ?? ""),
            currency: String(data.get("currency") ?? ""),
            active: activeInput?.checked ?? false,
          });
        }}
        className="formCol"
      >
        <input
          aria-label="Editar nombre producto"
          placeholder="Nombre"
          name="name"
          defaultValue={selectedProduct?.name ?? ""}
          disabled={!selectedProductId}
        />
        <input
          aria-label="Editar descripción producto"
          placeholder="Descripción"
          name="description"
          defaultValue={selectedProduct?.description ?? ""}
          disabled={!selectedProductId}
        />
        <input
          aria-label="Editar precio (centavos)"
          inputMode="numeric"
          name="priceCents"
          defaultValue={selectedProduct ? String(selectedProduct.priceCents) : ""}
          disabled={!selectedProductId}
        />
        <input
          aria-label="Editar moneda"
          name="currency"
          defaultValue={selectedProduct?.currency ?? "COP"}
          disabled={!selectedProductId}
        />
        <label className="inline">
          <input
            aria-label="Editar producto activo"
            type="checkbox"
            name="active"
            defaultChecked={selectedProduct?.active ?? false}
            disabled={!selectedProductId}
          />
          Activo
        </label>
        <div className="formRow">
          <button type="submit" disabled={!selectedProductId}>
            Guardar
          </button>
          <button
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

      <p className="muted">Seleccionado: {selectedProduct ? selectedProduct.name : "—"}</p>
    </div>
  );
}
