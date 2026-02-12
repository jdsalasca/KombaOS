import { useMemo, useState } from "react";
import type { InventoryMovementType, Material } from "../../lib/types";
import { useInventory } from "./useInventory";

type Props = {
  selectedMaterial: Material | null;
  selectedMaterialId: string | null;
};

export function InventoryPanel({ selectedMaterial, selectedMaterialId }: Props) {
  const { stock, stockState, threshold, movements, movementsState, lowStockAlerts, lowStockAlertsState, actions } =
    useInventory(selectedMaterialId);

  const [createMoveType, setCreateMoveType] = useState<InventoryMovementType>("IN");
  const [createMoveQty, setCreateMoveQty] = useState("");
  const [createMoveReason, setCreateMoveReason] = useState("");

  const hasLowStockAlert = useMemo(() => {
    if (!selectedMaterialId) return false;
    return lowStockAlerts.some((a) => a.materialId === selectedMaterialId);
  }, [lowStockAlerts, selectedMaterialId]);

  return (
    <div className="panel">
      <h2>Inventario</h2>
      <p className="muted">Material: {selectedMaterial ? `${selectedMaterial.name} (${selectedMaterial.unit})` : "—"}</p>

      <div className="kv">
        <div className="kv__row">
          <span className="kv__key">Stock</span>
          <span className="kv__value">
            {stockState.status === "loading" && "Cargando..."}
            {stockState.status === "error" && `Error: ${stockState.message}`}
            {stockState.status === "loaded" && stock ? stock.stock : stockState.status === "idle" ? "—" : ""}
          </span>
        </div>

        <div className="kv__row">
          <span className="kv__key">Alerta stock mínimo</span>
          <span className="kv__value">
            {lowStockAlertsState.status === "loading" && "Cargando..."}
            {lowStockAlertsState.status === "error" && `Error: ${lowStockAlertsState.message}`}
            {lowStockAlertsState.status === "loaded" && selectedMaterialId ? (hasLowStockAlert ? "BAJO" : "OK") : "—"}
          </span>
        </div>
      </div>

      <h3>Stock mínimo</h3>
      <form
        key={selectedMaterialId ?? "none"}
        onSubmit={async (e) => {
          e.preventDefault();
          const form = e.currentTarget as HTMLFormElement;
          const data = new FormData(form);
          await actions.saveThreshold(String(data.get("minStock") ?? ""));
        }}
        className="formRow"
      >
        <input
          aria-label="Stock mínimo"
          inputMode="decimal"
          name="minStock"
          defaultValue={threshold ? String(threshold.minStock) : ""}
        />
        <button type="submit" disabled={!selectedMaterialId}>
          Guardar umbral
        </button>
        <button type="button" onClick={actions.deleteThreshold} disabled={!selectedMaterialId || !threshold}>
          Eliminar umbral
        </button>
      </form>

      <h3>Registrar movimiento</h3>
      <form
        onSubmit={async (e) => {
          e.preventDefault();
          await actions.createMovement(createMoveType, createMoveQty, createMoveReason);
          setCreateMoveQty("");
          setCreateMoveReason("");
        }}
        className="formRow"
      >
        <select value={createMoveType} onChange={(e) => setCreateMoveType(e.target.value as InventoryMovementType)}>
          <option value="IN">IN</option>
          <option value="OUT">OUT</option>
          <option value="ADJUST">ADJUST</option>
        </select>
        <input
          aria-label="Cantidad movimiento"
          value={createMoveQty}
          onChange={(e) => setCreateMoveQty(e.target.value)}
          inputMode="decimal"
        />
        <input
          aria-label="Motivo movimiento"
          placeholder="Motivo (opcional)"
          value={createMoveReason}
          onChange={(e) => setCreateMoveReason(e.target.value)}
        />
        <button type="submit" disabled={!selectedMaterialId}>
          Crear movimiento
        </button>
      </form>

      <h3>Movimientos</h3>
      {movementsState.status === "loading" && <p className="muted">Cargando...</p>}
      {movementsState.status === "error" && <p className="error">Error: {movementsState.message}</p>}
      <ul className="list">
        {movements.map((m) => (
          <li key={m.id} className="list__item">
            <div className="movement">
              <span className="movement__type">{m.type}</span>
              <span className="movement__qty">
                {m.quantity} {selectedMaterial?.unit ?? ""}
              </span>
              {m.reason ? <span className="movement__reason">— {m.reason}</span> : null}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
