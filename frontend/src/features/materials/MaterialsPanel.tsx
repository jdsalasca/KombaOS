import { useState } from "react";
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

  const [createName, setCreateName] = useState("");
  const [createUnit, setCreateUnit] = useState("");
  const [createSupplier, setCreateSupplier] = useState("");
  const [createOrigin, setCreateOrigin] = useState("");
  const [createCertified, setCreateCertified] = useState(false);
  const [createCostCents, setCreateCostCents] = useState("");
  const [createCurrency, setCreateCurrency] = useState("COP");
  const [createError, setCreateError] = useState("");
  const [updateError, setUpdateError] = useState("");

  function validateMaterialInput(input: MaterialFormInput) {
    if (!input.name.trim().length) return "Ingresa el nombre del material.";
    if (!input.unit.trim().length) return "Ingresa la unidad del material.";
    const costRaw = input.costCents.trim();
    if (costRaw.length) {
      const parsed = Number(costRaw);
      if (!Number.isFinite(parsed) || parsed < 0) return "El costo debe ser un número válido.";
    }
    const currencyRaw = input.currency.trim();
    if (currencyRaw.length && !/^[A-Za-z]{3}$/.test(currencyRaw)) return "La moneda debe tener 3 letras.";
    return "";
  }

  async function onCreateMaterial(e: React.FormEvent) {
    e.preventDefault();
    const validation = validateMaterialInput({
      name: createName,
      unit: createUnit,
      supplier: createSupplier,
      origin: createOrigin,
      certified: createCertified,
      costCents: createCostCents,
      currency: createCurrency,
    });
    if (validation) {
      setCreateError(validation);
      return;
    }
    setCreateError("");
    await actions.create({
      name: createName,
      unit: createUnit,
      supplier: createSupplier,
      origin: createOrigin,
      certified: createCertified,
      costCents: createCostCents,
      currency: createCurrency,
    });
    setCreateName("");
    setCreateUnit("");
    setCreateSupplier("");
    setCreateOrigin("");
    setCreateCertified(false);
    setCreateCostCents("");
    setCreateCurrency("COP");
  }

  async function onUpdateMaterial(e: React.FormEvent) {
    e.preventDefault();
    if (!selectedMaterialId) return;
    const form = e.currentTarget as HTMLFormElement;
    const data = new FormData(form);
    const certifiedInput = form.elements.namedItem("certified") as HTMLInputElement | null;
    const input = {
      name: String(data.get("name") ?? ""),
      unit: String(data.get("unit") ?? ""),
      supplier: String(data.get("supplier") ?? ""),
      origin: String(data.get("origin") ?? ""),
      certified: certifiedInput?.checked ?? false,
      costCents: String(data.get("costCents") ?? ""),
      currency: String(data.get("currency") ?? ""),
    };
    const validation = validateMaterialInput(input);
    if (validation) {
      setUpdateError(validation);
      return;
    }
    setUpdateError("");
    await actions.update(selectedMaterialId, input);
  }

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
          <form onSubmit={onCreateMaterial} className="formRow">
            <label className="field">
              <span className="field__label">Nombre</span>
              <input
                className="field__input"
                aria-label="Nombre material"
                placeholder="Ej. Algodón"
                value={createName}
                onChange={(e) => setCreateName(e.target.value)}
              />
            </label>
            <label className="field">
              <span className="field__label">Unidad</span>
              <input
                className="field__input"
                aria-label="Unidad material"
                placeholder="Ej. kg"
                value={createUnit}
                onChange={(e) => setCreateUnit(e.target.value)}
              />
            </label>
            <label className="field">
              <span className="field__label">Proveedor</span>
              <input
                className="field__input"
                aria-label="Proveedor material"
                placeholder="Ej. Proveedor Andes"
                value={createSupplier}
                onChange={(e) => setCreateSupplier(e.target.value)}
              />
            </label>
            <label className="field">
              <span className="field__label">Origen</span>
              <input
                className="field__input"
                aria-label="Origen material"
                placeholder="Ej. Colombia"
                value={createOrigin}
                onChange={(e) => setCreateOrigin(e.target.value)}
              />
            </label>
            <label className="field">
              <span className="field__label">Costo</span>
              <input
                className="field__input"
                aria-label="Costo material (centavos)"
                placeholder="Ej. 1200"
                inputMode="numeric"
                value={createCostCents}
                onChange={(e) => setCreateCostCents(e.target.value)}
              />
              <span className="field__hint">Valor en centavos.</span>
            </label>
            <label className="field">
              <span className="field__label">Moneda</span>
              <input
                className="field__input"
                aria-label="Moneda material"
                placeholder="COP"
                value={createCurrency}
                onChange={(e) => setCreateCurrency(e.target.value)}
              />
            </label>
            <label className="field inline">
              <input
                aria-label="Material certificado"
                type="checkbox"
                checked={createCertified}
                onChange={(e) => setCreateCertified(e.target.checked)}
              />
              Certificado
            </label>
            <button className="button button--primary" type="submit">
              Crear material
            </button>
          </form>
          {createError ? <p className="field__error">{createError}</p> : null}
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
        <form key={selectedMaterialId ?? "none"} onSubmit={onUpdateMaterial} className="formCol">
          <div className="formRow">
            <label className="field">
              <span className="field__label">Nombre</span>
              <input
                className="field__input"
                aria-label="Editar nombre material"
                placeholder="Nombre"
                name="name"
                defaultValue={selectedMaterial?.name ?? ""}
                disabled={!selectedMaterialId}
              />
            </label>
            <label className="field">
              <span className="field__label">Unidad</span>
              <input
                className="field__input"
                aria-label="Editar unidad material"
                placeholder="Unidad"
                name="unit"
                defaultValue={selectedMaterial?.unit ?? ""}
                disabled={!selectedMaterialId}
              />
            </label>
            <label className="field">
              <span className="field__label">Proveedor</span>
              <input
                className="field__input"
                aria-label="Editar proveedor material"
                placeholder="Proveedor"
                name="supplier"
                defaultValue={selectedMaterial?.supplier ?? ""}
                disabled={!selectedMaterialId}
              />
            </label>
            <label className="field">
              <span className="field__label">Origen</span>
              <input
                className="field__input"
                aria-label="Editar origen material"
                placeholder="Origen"
                name="origin"
                defaultValue={selectedMaterial?.origin ?? ""}
                disabled={!selectedMaterialId}
              />
            </label>
            <label className="field">
              <span className="field__label">Costo</span>
              <input
                className="field__input"
                aria-label="Editar costo material (centavos)"
                placeholder="Costo (centavos)"
                inputMode="numeric"
                name="costCents"
                defaultValue={selectedMaterial?.costCents != null ? String(selectedMaterial.costCents) : ""}
                disabled={!selectedMaterialId}
              />
            </label>
            <label className="field">
              <span className="field__label">Moneda</span>
              <input
                className="field__input"
                aria-label="Editar moneda costo material"
                placeholder="COP"
                name="currency"
                defaultValue={selectedMaterial?.currency ?? "COP"}
                disabled={!selectedMaterialId}
              />
            </label>
            <label className="field inline">
              <input
                aria-label="Editar material certificado"
                type="checkbox"
                name="certified"
                defaultChecked={selectedMaterial?.certified ?? false}
                disabled={!selectedMaterialId}
              />
              Certificado
            </label>
          </div>
          {updateError ? <p className="field__error">{updateError}</p> : null}
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
