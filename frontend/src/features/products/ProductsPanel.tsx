import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { formatCopFromCents, parseCopInputToCents } from "../../lib/types";
import { useProducts } from "./useProducts";

export function ProductsPanel() {
  const { products, productsState, selectedProductId, setSelectedProductId, selectedProduct, actions, createState, editState } = useProducts();

  type ProductFormInput = {
    name: string;
    description: string;
    priceCop: string;
    active: boolean;
  };

  const createForm = useForm<ProductFormInput>({
    defaultValues: {
      name: "",
      description: "",
      priceCop: "",
      active: true,
    },
    mode: "onBlur",
  });

  const updateForm = useForm<ProductFormInput>({
    defaultValues: {
      name: "",
      description: "",
      priceCop: "",
      active: false,
    },
    mode: "onBlur",
  });

  useEffect(() => {
    updateForm.reset({
      name: selectedProduct?.name ?? "",
      description: selectedProduct?.description ?? "",
      priceCop: selectedProduct ? String(Math.round(selectedProduct.priceCents / 100)) : "",
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
        <span className="pill">{products.filter((p) => p.active).length} activos</span>
      </div>

      <div className="formRow">
        <button
          className="button button--ghost"
          type="button"
          onClick={actions.reload}
          disabled={productsState.status === "loading"}
        >
          Recargar lista
        </button>
      </div>

      <div className="form">
        <div>
          <h3 className="panel__title">Crear producto</h3>
          <form
            onSubmit={createForm.handleSubmit(async (values) => {
              await actions.create({
                name: values.name.trim(),
                description: values.description.trim(),
                priceCop: values.priceCop.trim(),
                active: values.active,
              });
              createForm.reset({
                name: "",
                description: "",
                priceCop: "",
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
                  maxLength: { value: 200, message: "Máximo 200 caracteres." },
                })}
              />
              {createForm.formState.errors.name ? (
                <span className="field__error">{createForm.formState.errors.name.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Descripción</span>
              <input
                className={
                  createForm.formState.errors.description ? "field__input field__input--error" : "field__input"
                }
                aria-label="Descripción producto"
                placeholder="Ej. Hecha a mano"
                {...createForm.register("description", {
                  required: "Ingresa una descripción.",
                  maxLength: { value: 2000, message: "Máximo 2000 caracteres." },
                })}
              />
              {createForm.formState.errors.description ? (
                <span className="field__error">{createForm.formState.errors.description.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Precio (COP)</span>
              <input
                className={
                  createForm.formState.errors.priceCop ? "field__input field__input--error" : "field__input"
                }
                aria-label="Precio (COP)"
                inputMode="numeric"
                {...createForm.register("priceCop", {
                  required: "Ingresa el precio del producto.",
                  validate: (value) => {
                    const cents = parseCopInputToCents(value);
                    if (cents == null) return "Ingresa un valor en pesos (sin decimales).";
                    if (cents > 999_999_999) return "El precio es demasiado alto.";
                    return true;
                  },
                })}
              />
              <span className="field__hint">Pesos colombianos (ej. 120000).</span>
              {createForm.formState.errors.priceCop ? (
                <span className="field__error">{createForm.formState.errors.priceCop.message}</span>
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
            <button
              className="button button--primary"
              type="submit"
              disabled={createForm.formState.isSubmitting || createState.status === "loading"}
            >
              Crear producto
            </button>
            {createState.status === "loading" && <p className="muted">Guardando producto...</p>}
            {createState.status === "error" && <p className="error">{createState.message}</p>}
          </form>
        </div>
      </div>

      {productsState.status === "loading" && <p className="muted">Cargando productos...</p>}
      {productsState.status === "error" && <p className="error">{productsState.message}</p>}
      {productsState.status === "loaded" && !products.length && (
        <p className="muted">Aún no hay productos registrados. Crea el primero con nombre, descripción y precio.</p>
      )}

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
                {(formatCopFromCents(p.priceCents) ?? `${p.currency} ${p.priceCents / 100}`) + ` · ${p.active ? "Activo" : "Inactivo"}`}
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
              priceCop: values.priceCop.trim(),
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
                  maxLength: { value: 200, message: "Máximo 200 caracteres." },
                })}
              />
              {updateForm.formState.errors.name ? (
                <span className="field__error">{updateForm.formState.errors.name.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Descripción</span>
              <input
                className={
                  updateForm.formState.errors.description ? "field__input field__input--error" : "field__input"
                }
                aria-label="Editar descripción producto"
                placeholder="Descripción"
                disabled={!selectedProductId}
                {...updateForm.register("description", {
                  required: "Ingresa una descripción.",
                  maxLength: { value: 2000, message: "Máximo 2000 caracteres." },
                })}
              />
              {updateForm.formState.errors.description ? (
                <span className="field__error">{updateForm.formState.errors.description.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Precio (COP)</span>
              <input
                className={
                  updateForm.formState.errors.priceCop ? "field__input field__input--error" : "field__input"
                }
                aria-label="Editar precio (COP)"
                inputMode="numeric"
                disabled={!selectedProductId}
                {...updateForm.register("priceCop", {
                  required: "Ingresa el precio del producto.",
                  validate: (value) => {
                    const cents = parseCopInputToCents(value);
                    if (cents == null) return "Ingresa un valor en pesos (sin decimales).";
                    if (cents > 999_999_999) return "El precio es demasiado alto.";
                    return true;
                  },
                })}
              />
              {updateForm.formState.errors.priceCop ? (
                <span className="field__error">{updateForm.formState.errors.priceCop.message}</span>
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
            <button
              className="button button--primary"
              type="submit"
              disabled={!selectedProductId || updateForm.formState.isSubmitting || editState.status === "loading"}
            >
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
              disabled={!selectedProductId || updateForm.formState.isSubmitting || editState.status === "loading"}
            >
              Eliminar
            </button>
            {editState.status === "loading" && <p className="muted">Actualizando producto...</p>}
            {editState.status === "error" && <p className="error">{editState.message}</p>}
          </div>
        </form>
      </div>

      <p className="muted">Seleccionado: {selectedProduct ? selectedProduct.name : "—"}</p>
    </div>
  );
}
