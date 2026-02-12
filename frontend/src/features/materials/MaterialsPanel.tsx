import { useEffect } from "react";
import { useForm } from "react-hook-form";
import type { Material } from "../../lib/types";
import type { MaterialsFiltersState } from "./useMaterials";
import type { LoadState } from "../../lib/types";

type Props = {
  materials: Material[];
  materialsState: LoadState;
  filtersDraft: MaterialsFiltersState;
  setFiltersDraft: (next: MaterialsFiltersState) => void;
  actions: {
    applyFilters: () => void;
    clearFilters: () => void;
    create: (input: {
      name: string;
      unit: string;
      supplier: string;
      origin: string;
      certified: boolean;
      costCents: string;
      currency: string;
    }) => Promise<void>;
    update: (
      materialId: string,
      input: {
        name: string;
        unit: string;
        supplier: string;
        origin: string;
        certified: boolean;
        costCents: string;
        currency: string;
      },
    ) => Promise<void>;
    remove: (materialId: string) => Promise<void>;
  };

  selectedMaterialId: string | null;
  selectedMaterial: Material | null;
  onSelectMaterialId: (id: string | null) => void;
};

export function MaterialsPanel({
  materials,
  materialsState,
  filtersDraft,
  setFiltersDraft,
  actions,
  selectedMaterialId,
  selectedMaterial,
  onSelectMaterialId,
}: Props) {
  type MaterialFormInput = {
    name: string;
    unit: string;
    supplier: string;
    origin: string;
    certified: boolean;
    costCents: string;
    currency: string;
  };

  const createForm = useForm<MaterialFormInput>({
    defaultValues: {
      name: "",
      unit: "",
      supplier: "",
      origin: "",
      certified: false,
      costCents: "",
      currency: "COP",
    },
    mode: "onBlur",
  });

  const updateForm = useForm<MaterialFormInput>({
    defaultValues: {
      name: "",
      unit: "",
      supplier: "",
      origin: "",
      certified: false,
      costCents: "",
      currency: "COP",
    },
    mode: "onBlur",
  });

  useEffect(() => {
    updateForm.reset({
      name: selectedMaterial?.name ?? "",
      unit: selectedMaterial?.unit ?? "",
      supplier: selectedMaterial?.supplier ?? "",
      origin: selectedMaterial?.origin ?? "",
      certified: selectedMaterial?.certified ?? false,
      costCents: selectedMaterial?.costCents != null ? String(selectedMaterial.costCents) : "",
      currency: selectedMaterial?.currency ?? "COP",
    });
  }, [selectedMaterial, updateForm]);

  async function onDeleteMaterial() {
    if (!selectedMaterialId) return;
    await actions.remove(selectedMaterialId);
    onSelectMaterialId(null);
  }

  function renderMeta(m: Material) {
    const parts: string[] = [];
    if (m.supplier) parts.push(m.supplier);
    if (m.origin) parts.push(m.origin);
    if (m.certified) parts.push("Certificado");
    if (m.costCents != null && m.currency) parts.push(`${m.currency} ${m.costCents / 100}`);
    return parts.join(" · ");
  }

  return (
    <div className="card panel">
      <div className="panel__header">
        <div>
          <h2 className="panel__title">Materiales</h2>
          <p className="panel__subtitle">Crea y selecciona insumos para operar el inventario.</p>
        </div>
        <span className="pill">{materials.length} registrados</span>
      </div>

      <div className="form">
        <div>
          <h3 className="panel__title">Crear material</h3>
          <form
            onSubmit={createForm.handleSubmit(async (values) => {
              await actions.create({
                name: values.name.trim(),
                unit: values.unit.trim(),
                supplier: values.supplier.trim(),
                origin: values.origin.trim(),
                certified: values.certified,
                costCents: values.costCents.trim(),
                currency: values.currency.trim().toUpperCase(),
              });
              createForm.reset({
                name: "",
                unit: "",
                supplier: "",
                origin: "",
                certified: false,
                costCents: "",
                currency: "COP",
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
                aria-label="Nombre material"
                placeholder="Ej. Algodón"
                {...createForm.register("name", {
                  required: "Ingresa el nombre del material.",
                })}
              />
              {createForm.formState.errors.name ? (
                <span className="field__error">{createForm.formState.errors.name.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Unidad</span>
              <input
                className={
                  createForm.formState.errors.unit ? "field__input field__input--error" : "field__input"
                }
                aria-label="Unidad material"
                placeholder="Ej. kg"
                {...createForm.register("unit", {
                  required: "Ingresa la unidad del material.",
                })}
              />
              {createForm.formState.errors.unit ? (
                <span className="field__error">{createForm.formState.errors.unit.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Proveedor</span>
              <input
                className="field__input"
                aria-label="Proveedor material"
                placeholder="Ej. Proveedor Andes"
                {...createForm.register("supplier")}
              />
            </label>
            <label className="field">
              <span className="field__label">Origen</span>
              <input
                className="field__input"
                aria-label="Origen material"
                placeholder="Ej. Colombia"
                {...createForm.register("origin")}
              />
            </label>
            <label className="field">
              <span className="field__label">Costo</span>
              <input
                className={
                  createForm.formState.errors.costCents ? "field__input field__input--error" : "field__input"
                }
                aria-label="Costo material (centavos)"
                placeholder="Ej. 1200"
                inputMode="numeric"
                {...createForm.register("costCents", {
                  validate: (value) => {
                    const trimmed = value.trim();
                    if (!trimmed) return true;
                    const parsed = Number(trimmed);
                    if (!Number.isFinite(parsed) || parsed < 0) {
                      return "El costo debe ser un número válido.";
                    }
                    return true;
                  },
                })}
              />
              <span className="field__hint">Valor en centavos.</span>
              {createForm.formState.errors.costCents ? (
                <span className="field__error">{createForm.formState.errors.costCents.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Moneda</span>
              <input
                className={
                  createForm.formState.errors.currency ? "field__input field__input--error" : "field__input"
                }
                aria-label="Moneda material"
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
                aria-label="Material certificado"
                type="checkbox"
                {...createForm.register("certified")}
              />
              Certificado
            </label>
            <button className="button button--primary" type="submit">
              Crear material
            </button>
          </form>
        </div>

        <div>
          <h3 className="panel__title">Buscar materiales</h3>
          <form
            className="formRow"
            onSubmit={(e) => {
              e.preventDefault();
              actions.applyFilters();
            }}
          >
            <label className="field">
              <span className="field__label">Nombre</span>
              <input
                className="field__input"
                aria-label="Buscar material"
                placeholder="Buscar por nombre"
                value={filtersDraft.q}
                onChange={(e) => setFiltersDraft({ ...filtersDraft, q: e.target.value })}
              />
            </label>
            <label className="field">
              <span className="field__label">Proveedor</span>
              <input
                className="field__input"
                aria-label="Filtrar por proveedor"
                placeholder="Proveedor"
                value={filtersDraft.supplier}
                onChange={(e) => setFiltersDraft({ ...filtersDraft, supplier: e.target.value })}
              />
            </label>
            <label className="field">
              <span className="field__label">Origen</span>
              <input
                className="field__input"
                aria-label="Filtrar por origen"
                placeholder="Origen"
                value={filtersDraft.origin}
                onChange={(e) => setFiltersDraft({ ...filtersDraft, origin: e.target.value })}
              />
            </label>
            <label className="field">
              <span className="field__label">Certificación</span>
              <select
                aria-label="Filtrar por certificación"
                value={filtersDraft.certified}
                onChange={(e) => setFiltersDraft({ ...filtersDraft, certified: e.target.value as MaterialsFiltersState["certified"] })}
              >
                <option value="">Todas</option>
                <option value="true">Certificado</option>
                <option value="false">No certificado</option>
              </select>
            </label>
            <button className="button" type="submit">
              Aplicar filtros
            </button>
            <button className="button button--ghost" type="button" onClick={actions.clearFilters}>
              Limpiar
            </button>
          </form>
        </div>
      </div>

      {materialsState.status === "loading" && <p className="muted">Cargando materiales...</p>}
      {materialsState.status === "error" && <p className="error">{materialsState.message}</p>}
      {materialsState.status === "loaded" && !materials.length && <p className="muted">Aún no hay materiales registrados.</p>}

      <ul className="list">
        {materials.map((m) => (
          <li key={m.id} className="list__item">
            <button
              type="button"
              className={m.id === selectedMaterialId ? "listButton listButton--active" : "listButton"}
              onClick={() => onSelectMaterialId(m.id)}
            >
              <span className="listButton__title">
                {m.name} ({m.unit})
              </span>
              <span className="listButton__meta">{renderMeta(m)}</span>
            </button>
          </li>
        ))}
      </ul>

      <div>
        <h3 className="panel__title">Editar material</h3>
        <form
          key={selectedMaterialId ?? "none"}
          onSubmit={updateForm.handleSubmit(async (values) => {
            if (!selectedMaterialId) return;
            await actions.update(selectedMaterialId, {
              name: values.name.trim(),
              unit: values.unit.trim(),
              supplier: values.supplier.trim(),
              origin: values.origin.trim(),
              certified: values.certified,
              costCents: values.costCents.trim(),
              currency: values.currency.trim().toUpperCase(),
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
                aria-label="Editar nombre material"
                placeholder="Nombre"
                disabled={!selectedMaterialId}
                {...updateForm.register("name", {
                  required: "Ingresa el nombre del material.",
                })}
              />
              {updateForm.formState.errors.name ? (
                <span className="field__error">{updateForm.formState.errors.name.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Unidad</span>
              <input
                className={
                  updateForm.formState.errors.unit ? "field__input field__input--error" : "field__input"
                }
                aria-label="Editar unidad material"
                placeholder="Unidad"
                disabled={!selectedMaterialId}
                {...updateForm.register("unit", {
                  required: "Ingresa la unidad del material.",
                })}
              />
              {updateForm.formState.errors.unit ? (
                <span className="field__error">{updateForm.formState.errors.unit.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Proveedor</span>
              <input
                className="field__input"
                aria-label="Editar proveedor material"
                placeholder="Proveedor"
                disabled={!selectedMaterialId}
                {...updateForm.register("supplier")}
              />
            </label>
            <label className="field">
              <span className="field__label">Origen</span>
              <input
                className="field__input"
                aria-label="Editar origen material"
                placeholder="Origen"
                disabled={!selectedMaterialId}
                {...updateForm.register("origin")}
              />
            </label>
            <label className="field">
              <span className="field__label">Costo</span>
              <input
                className={
                  updateForm.formState.errors.costCents ? "field__input field__input--error" : "field__input"
                }
                aria-label="Editar costo material (centavos)"
                placeholder="Costo (centavos)"
                inputMode="numeric"
                disabled={!selectedMaterialId}
                {...updateForm.register("costCents", {
                  validate: (value) => {
                    const trimmed = value.trim();
                    if (!trimmed) return true;
                    const parsed = Number(trimmed);
                    if (!Number.isFinite(parsed) || parsed < 0) {
                      return "El costo debe ser un número válido.";
                    }
                    return true;
                  },
                })}
              />
              {updateForm.formState.errors.costCents ? (
                <span className="field__error">{updateForm.formState.errors.costCents.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Moneda</span>
              <input
                className={
                  updateForm.formState.errors.currency ? "field__input field__input--error" : "field__input"
                }
                aria-label="Editar moneda costo material"
                placeholder="COP"
                disabled={!selectedMaterialId}
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
                aria-label="Editar material certificado"
                type="checkbox"
                disabled={!selectedMaterialId}
                {...updateForm.register("certified")}
              />
              Certificado
            </label>
          </div>
          <div className="formRow">
            <button className="button button--primary" type="submit" disabled={!selectedMaterialId}>
              Guardar cambios
            </button>
            <button className="button button--danger" type="button" onClick={onDeleteMaterial} disabled={!selectedMaterialId}>
              Eliminar material
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
