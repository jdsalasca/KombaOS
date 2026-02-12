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

  const [createName, setCreateName] = useState("");
  const [createUnit, setCreateUnit] = useState("");
  const [createSupplier, setCreateSupplier] = useState("");
  const [createOrigin, setCreateOrigin] = useState("");
  const [createCertified, setCreateCertified] = useState(false);
  const [createCostCents, setCreateCostCents] = useState("");
  const [createCurrency, setCreateCurrency] = useState("COP");

  async function onCreateMaterial(e: React.FormEvent) {
    e.preventDefault();
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
    await actions.update(selectedMaterialId, {
      name: String(data.get("name") ?? ""),
      unit: String(data.get("unit") ?? ""),
      supplier: String(data.get("supplier") ?? ""),
      origin: String(data.get("origin") ?? ""),
      certified: certifiedInput?.checked ?? false,
      costCents: String(data.get("costCents") ?? ""),
      currency: String(data.get("currency") ?? ""),
    });
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
    <div className="panel">
      <h2>Materiales</h2>

      <h3>Crear</h3>
      <form onSubmit={onCreateMaterial} className="formRow">
        <input
          aria-label="Nombre material"
          placeholder="Nombre"
          value={createName}
          onChange={(e) => setCreateName(e.target.value)}
        />
        <input
          aria-label="Unidad material"
          placeholder="Unidad"
          value={createUnit}
          onChange={(e) => setCreateUnit(e.target.value)}
        />
        <input
          aria-label="Proveedor material"
          placeholder="Proveedor"
          value={createSupplier}
          onChange={(e) => setCreateSupplier(e.target.value)}
        />
        <input
          aria-label="Origen material"
          placeholder="Origen"
          value={createOrigin}
          onChange={(e) => setCreateOrigin(e.target.value)}
        />
        <input
          aria-label="Costo material (centavos)"
          placeholder="Costo (centavos)"
          inputMode="numeric"
          value={createCostCents}
          onChange={(e) => setCreateCostCents(e.target.value)}
        />
        <input
          aria-label="Moneda material"
          placeholder="COP"
          value={createCurrency}
          onChange={(e) => setCreateCurrency(e.target.value)}
        />
        <label className="inline">
          <input
            aria-label="Material certificado"
            type="checkbox"
            checked={createCertified}
            onChange={(e) => setCreateCertified(e.target.checked)}
          />
          Certificado
        </label>
        <button type="submit">Crear</button>
      </form>

      <h3>Filtros</h3>
      <form
        className="formRow"
        onSubmit={(e) => {
          e.preventDefault();
          actions.applyFilters();
        }}
      >
        <input
          aria-label="Buscar material"
          placeholder="Buscar por nombre"
          value={filtersDraft.q}
          onChange={(e) => setFiltersDraft({ ...filtersDraft, q: e.target.value })}
        />
        <input
          aria-label="Filtrar por proveedor"
          placeholder="Proveedor"
          value={filtersDraft.supplier}
          onChange={(e) => setFiltersDraft({ ...filtersDraft, supplier: e.target.value })}
        />
        <input
          aria-label="Filtrar por origen"
          placeholder="Origen"
          value={filtersDraft.origin}
          onChange={(e) => setFiltersDraft({ ...filtersDraft, origin: e.target.value })}
        />
        <select
          aria-label="Filtrar por certificación"
          value={filtersDraft.certified}
          onChange={(e) => setFiltersDraft({ ...filtersDraft, certified: e.target.value as MaterialsFiltersState["certified"] })}
        >
          <option value="">Certificación: todas</option>
          <option value="true">Certificado</option>
          <option value="false">No certificado</option>
        </select>
        <button type="submit">Aplicar</button>
        <button type="button" onClick={actions.clearFilters}>
          Limpiar
        </button>
      </form>

      {materialsState.status === "loading" && <p className="muted">Cargando...</p>}
      {materialsState.status === "error" && <p className="error">Error: {materialsState.message}</p>}

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

      <h3>Ficha material</h3>
      <form key={selectedMaterialId ?? "none"} onSubmit={onUpdateMaterial} className="formCol">
        <div className="formRow">
          <input
            aria-label="Editar nombre material"
            placeholder="Nombre"
            name="name"
            defaultValue={selectedMaterial?.name ?? ""}
            disabled={!selectedMaterialId}
          />
          <input
            aria-label="Editar unidad material"
            placeholder="Unidad"
            name="unit"
            defaultValue={selectedMaterial?.unit ?? ""}
            disabled={!selectedMaterialId}
          />
          <input
            aria-label="Editar proveedor material"
            placeholder="Proveedor"
            name="supplier"
            defaultValue={selectedMaterial?.supplier ?? ""}
            disabled={!selectedMaterialId}
          />
          <input
            aria-label="Editar origen material"
            placeholder="Origen"
            name="origin"
            defaultValue={selectedMaterial?.origin ?? ""}
            disabled={!selectedMaterialId}
          />
          <input
            aria-label="Editar costo material (centavos)"
            placeholder="Costo (centavos)"
            inputMode="numeric"
            name="costCents"
            defaultValue={selectedMaterial?.costCents != null ? String(selectedMaterial.costCents) : ""}
            disabled={!selectedMaterialId}
          />
          <input
            aria-label="Editar moneda costo material"
            placeholder="COP"
            name="currency"
            defaultValue={selectedMaterial?.currency ?? "COP"}
            disabled={!selectedMaterialId}
          />
          <label className="inline">
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
        <div className="formRow">
          <button type="submit" disabled={!selectedMaterialId}>
            Guardar material
          </button>
          <button type="button" onClick={onDeleteMaterial} disabled={!selectedMaterialId}>
            Eliminar material
          </button>
        </div>
      </form>
    </div>
  );
}
