import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useProducts } from "./useProducts";

export function ProductsPanel() {
  const { products, productsState, selectedProductId, setSelectedProductId, selectedProduct, actions } = useProducts();

  type ProductFormInput = {
    name: string;
    description: string;
    priceCents: string;
    currency: string;
    active: boolean;
  };

  const createForm = useForm<ProductFormInput>({
    defaultValues: {
      name: "",
      description: "",
      priceCents: "",
      currency: "COP",
      active: true,
    },
    mode: "onBlur",
  });

  const updateForm = useForm<ProductFormInput>({
    defaultValues: {
      name: "",
      description: "",
      priceCents: "",
      currency: "COP",
      active: false,
    },
    mode: "onBlur",
  });

  useEffect(() => {
    updateForm.reset({
      name: selectedProduct?.name ?? "",
      description: selectedProduct?.description ?? "",
      priceCents: selectedProduct ? String(selectedProduct.priceCents) : "",
      currency: selectedProduct?.currency ?? "COP",
      active: selectedProduct?.active ?? false,
    });
  }, [selectedProduct, updateForm]);


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
            onSubmit={createForm.handleSubmit(async (values) => {
              await actions.create({
                name: values.name.trim(),
                description: values.description.trim(),
                priceCents: values.priceCents.trim(),
                currency: values.currency.trim().toUpperCase(),
                active: values.active,
              });
              createForm.reset({
                name: "",
                description: "",
                priceCents: "",
                currency: "COP",
                active: true,
              });
            })}
            className="formRow"
          >
            <label className="field">
              <span className="field__label">Nombre</span>
              <input
                className={
                  createForm.formState.errors.name ? "field__input field__input--error" : "field__input"
                }
                aria-label="Nombre producto"
                placeholder="Ej. Mesa de roble"
                {...createForm.register("name", {
                  required: "Ingresa el nombre del producto.",
                })}
              />
              {createForm.formState.errors.name ? (
                <span className="field__error">{createForm.formState.errors.name.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Descripción</span>
              <input
                className="field__input"
                aria-label="Descripción producto"
                placeholder="Ej. Hecha a mano"
                {...createForm.register("description")}
              />
            </label>
            <label className="field">
              <span className="field__label">Precio</span>
              <input
                className={
                  createForm.formState.errors.priceCents ? "field__input field__input--error" : "field__input"
                }
                aria-label="Precio (centavos)"
                inputMode="numeric"
                {...createForm.register("priceCents", {
                  required: "Ingresa el precio del producto.",
                  validate: (value) => {
                    const parsed = Number(value.trim());
                    if (!Number.isFinite(parsed) || parsed < 0) {
                      return "El precio debe ser un número válido.";
                    }
                    return true;
                  },
                })}
              />
              <span className="field__hint">Valor en centavos.</span>
              {createForm.formState.errors.priceCents ? (
                <span className="field__error">{createForm.formState.errors.priceCents.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Moneda</span>
              <input
                className={
                  createForm.formState.errors.currency ? "field__input field__input--error" : "field__input"
                }
                aria-label="Moneda"
                placeholder="COP"
                {...createForm.register("currency", {
                  required: "Ingresa la moneda.",
                  validate: (value) =>
                    /^[A-Za-z]{3}$/.test(value.trim()) || "La moneda debe tener 3 letras.",
                })}
              />
              {createForm.formState.errors.currency ? (
                <span className="field__error">{createForm.formState.errors.currency.message}</span>
              ) : null}
            </label>
            <label className="field inline">
              <input
                aria-label="Producto activo"
                type="checkbox"
                {...createForm.register("active")}
              />
              Producto activo
            </label>
            <button className="button button--primary" type="submit">
              Crear producto
            </button>
          </form>
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
          onSubmit={updateForm.handleSubmit(async (values) => {
            if (!selectedProductId) return;
            await actions.update(selectedProductId, {
              name: values.name.trim(),
              description: values.description.trim(),
              priceCents: values.priceCents.trim(),
              currency: values.currency.trim().toUpperCase(),
              active: values.active,
            });
          })}
          className="formCol"
        >
          <div className="formRow">
            <label className="field">
              <span className="field__label">Nombre</span>
              <input
                className={
                  updateForm.formState.errors.name ? "field__input field__input--error" : "field__input"
                }
                aria-label="Editar nombre producto"
                placeholder="Nombre"
                disabled={!selectedProductId}
                {...updateForm.register("name", {
                  required: "Ingresa el nombre del producto.",
                })}
              />
              {updateForm.formState.errors.name ? (
                <span className="field__error">{updateForm.formState.errors.name.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Descripción</span>
              <input
                className="field__input"
                aria-label="Editar descripción producto"
                placeholder="Descripción"
                disabled={!selectedProductId}
                {...updateForm.register("description")}
              />
            </label>
            <label className="field">
              <span className="field__label">Precio</span>
              <input
                className={
                  updateForm.formState.errors.priceCents ? "field__input field__input--error" : "field__input"
                }
                aria-label="Editar precio (centavos)"
                inputMode="numeric"
                disabled={!selectedProductId}
                {...updateForm.register("priceCents", {
                  required: "Ingresa el precio del producto.",
                  validate: (value) => {
                    const parsed = Number(value.trim());
                    if (!Number.isFinite(parsed) || parsed < 0) {
                      return "El precio debe ser un número válido.";
                    }
                    return true;
                  },
                })}
              />
              {updateForm.formState.errors.priceCents ? (
                <span className="field__error">{updateForm.formState.errors.priceCents.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Moneda</span>
              <input
                className={
                  updateForm.formState.errors.currency ? "field__input field__input--error" : "field__input"
                }
                aria-label="Editar moneda"
                disabled={!selectedProductId}
                {...updateForm.register("currency", {
                  required: "Ingresa la moneda.",
                  validate: (value) =>
                    /^[A-Za-z]{3}$/.test(value.trim()) || "La moneda debe tener 3 letras.",
                })}
              />
              {updateForm.formState.errors.currency ? (
                <span className="field__error">{updateForm.formState.errors.currency.message}</span>
              ) : null}
            </label>
            <label className="field inline">
              <input
                aria-label="Editar producto activo"
                type="checkbox"
                disabled={!selectedProductId}
                {...updateForm.register("active")}
              />
              Activo
            </label>
          </div>
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
