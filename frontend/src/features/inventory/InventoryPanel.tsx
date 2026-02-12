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
  const [thresholdError, setThresholdError] = useState("");
  const [movementError, setMovementError] = useState("");

  const hasLowStockAlert = useMemo(() => {
    if (!selectedMaterialId) return false;
    return lowStockAlerts.some((a) => a.materialId === selectedMaterialId);
  }, [lowStockAlerts, selectedMaterialId]);

  return (
    <div className="card panel">
      <div className="panel__header">
        <div>
          <h2 className="panel__title">Inventario</h2>
          <p className="panel__subtitle">Controla el stock y registra movimientos.</p>
        </div>
        <span className={hasLowStockAlert ? "pill pill--warning" : "pill pill--success"}>
          {selectedMaterialId ? (hasLowStockAlert ? "Stock bajo" : "Stock OK") : "Sin material"}
        </span>
      </div>

      <p className="muted">Material activo: {selectedMaterial ? `${selectedMaterial.name} (${selectedMaterial.unit})` : "—"}</p>

      <div className="kv">
        <div className="kv__row">
          <span className="kv__key">Stock disponible</span>
          <span>
            {stockState.status === "loading" && "Cargando..."}
            {stockState.status === "error" && `Error: ${stockState.message}`}
            {stockState.status === "loaded" && stock ? stock.stock : stockState.status === "idle" ? "—" : ""}
          </span>
        </div>

        <div className="kv__row">
          <span className="kv__key">Alerta stock mínimo</span>
          <span>
            {lowStockAlertsState.status === "loading" && "Cargando..."}
            {lowStockAlertsState.status === "error" && `Error: ${lowStockAlertsState.message}`}
            {lowStockAlertsState.status === "loaded" && selectedMaterialId ? (hasLowStockAlert ? "BAJO" : "OK") : "—"}
          </span>
        </div>
      </div>

      <div className="form">
        <div>
          <h3 className="panel__title">Definir stock mínimo</h3>
          <form
            key={selectedMaterialId ?? "none"}
            onSubmit={async (e) => {
              e.preventDefault();
              const form = e.currentTarget as HTMLFormElement;
              const data = new FormData(form);
              const value = String(data.get("minStock") ?? "").trim();
              if (!value.length) {
                setThresholdError("Ingresa un valor para el stock mínimo.");
                return;
              }
              const parsed = Number(value);
              if (!Number.isFinite(parsed) || parsed < 0) {
                setThresholdError("El stock mínimo debe ser un número válido.");
                return;
              }
              setThresholdError("");
              await actions.saveThreshold(value);
            }}
            className="formRow"
          >
            <label className="field">
              <span className="field__label">Stock mínimo</span>
              <input
                className="field__input"
                aria-label="Stock mínimo"
                inputMode="decimal"
                name="minStock"
                defaultValue={threshold ? String(threshold.minStock) : ""}
                disabled={!selectedMaterialId}
              />
            </label>
            <button className="button button--primary" type="submit" disabled={!selectedMaterialId}>
              Guardar umbral
            </button>
            <button className="button button--ghost" type="button" onClick={actions.deleteThreshold} disabled={!selectedMaterialId || !threshold}>
              Eliminar umbral
            </button>
          </form>
          {thresholdError ? <p className="field__error">{thresholdError}</p> : null}
        </div>

        <div>
          <h3 className="panel__title">Registrar movimiento</h3>
          <form
            onSubmit={async (e) => {
              e.preventDefault();
              if (!createMoveQty.trim().length) {
                setMovementError("Indica la cantidad a mover.");
                return;
              }
              const parsed = Number(createMoveQty.trim());
              if (!Number.isFinite(parsed) || parsed <= 0) {
                setMovementError("La cantidad debe ser mayor a cero.");
                return;
              }
              setMovementError("");
              await actions.createMovement(createMoveType, createMoveQty, createMoveReason);
              setCreateMoveQty("");
              setCreateMoveReason("");
            }}
            className="formRow"
          >
            <label className="field">
              <span className="field__label">Tipo</span>
              <select value={createMoveType} onChange={(e) => setCreateMoveType(e.target.value as InventoryMovementType)}>
                <option value="IN">Ingreso</option>
                <option value="OUT">Salida</option>
                <option value="ADJUST">Ajuste</option>
              </select>
            </label>
            <label className="field">
              <span className="field__label">Cantidad</span>
              <input
                className="field__input"
                aria-label="Cantidad movimiento"
                value={createMoveQty}
                onChange={(e) => setCreateMoveQty(e.target.value)}
                inputMode="decimal"
              />
            </label>
            <label className="field">
              <span className="field__label">Motivo</span>
              <input
                className="field__input"
                aria-label="Motivo movimiento"
                placeholder="Motivo (opcional)"
                value={createMoveReason}
                onChange={(e) => setCreateMoveReason(e.target.value)}
              />
            </label>
            <button className="button button--primary" type="submit" disabled={!selectedMaterialId}>
              Registrar
            </button>
          </form>
          {movementError ? <p className="field__error">{movementError}</p> : null}
        </div>
      </div>

      <div>
        <h3 className="panel__title">Historial de movimientos</h3>
        {movementsState.status === "loading" && <p className="muted">Cargando movimientos...</p>}
        {movementsState.status === "error" && <p className="error">{movementsState.message}</p>}
        {movementsState.status === "loaded" && !movements.length && <p className="muted">No hay movimientos registrados.</p>}
        <ul className="list">
          {movements.map((m) => (
            <li key={m.id} className="list__item">
              <div className="movement">
                <span className="movement__type">{m.type}</span>
                <span>
                  {m.quantity} {selectedMaterial?.unit ?? ""}
                </span>
                {m.reason ? <span className="movement__reason">— {m.reason}</span> : null}
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
