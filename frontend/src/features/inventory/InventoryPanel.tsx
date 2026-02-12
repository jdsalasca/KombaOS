import { useEffect, useMemo } from "react";
import { useForm } from "react-hook-form";
import type { InventoryMovementType, Material } from "../../lib/types";
import { useInventory } from "./useInventory";

type Props = {
  selectedMaterial: Material | null;
  selectedMaterialId: string | null;
};

export function InventoryPanel({ selectedMaterial, selectedMaterialId }: Props) {
  const {
    stock,
    stockState,
    threshold,
    movements,
    movementsState,
    lowStockAlerts,
    lowStockAlertsState,
    thresholdActionState,
    movementActionState,
    actions,
  } = useInventory(selectedMaterialId);

  const thresholdForm = useForm<{ minStock: string }>({
    defaultValues: { minStock: "" },
    mode: "onBlur",
  });

  const movementForm = useForm<{ type: InventoryMovementType; quantity: string; reason: string }>({
    defaultValues: { type: "IN", quantity: "", reason: "" },
    mode: "onBlur",
  });

  const hasLowStockAlert = useMemo(() => {
    if (!selectedMaterialId) return false;
    return lowStockAlerts.some((a) => a.materialId === selectedMaterialId);
  }, [lowStockAlerts, selectedMaterialId]);

  useEffect(() => {
    thresholdForm.reset({ minStock: threshold ? String(threshold.minStock) : "" });
  }, [threshold, thresholdForm]);

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

      {!selectedMaterialId && (
        <div className="card">
          <p className="muted">Selecciona o crea un material para habilitar stock, alertas y movimientos.</p>
          <a className="button" href="#materiales">
            Ir a materiales
          </a>
        </div>
      )}

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
            onSubmit={thresholdForm.handleSubmit(async (values) => {
              await actions.saveThreshold(values.minStock.trim());
            })}
            className="formRow"
          >
            <label className="field">
              <span className="field__label">Stock mínimo</span>
              <input
                className={
                  thresholdForm.formState.errors.minStock
                    ? "field__input field__input--error"
                    : "field__input"
                }
                aria-label="Stock mínimo"
                inputMode="decimal"
                disabled={!selectedMaterialId}
                {...thresholdForm.register("minStock", {
                  required: "Ingresa un valor para el stock mínimo.",
                  validate: (value) => {
                    const trimmed = value.trim();
                    const parsed = Number(trimmed);
                    if (!Number.isFinite(parsed) || parsed < 0) {
                      return "El stock mínimo debe ser un número válido.";
                    }
                    return true;
                  },
                })}
              />
              {thresholdForm.formState.errors.minStock ? (
                <span className="field__error">{thresholdForm.formState.errors.minStock.message}</span>
              ) : null}
            </label>
            <button
              className="button button--primary"
              type="submit"
              disabled={!selectedMaterialId || thresholdActionState.status === "loading"}
            >
              Guardar umbral
            </button>
            <button
              className="button button--ghost"
              type="button"
              onClick={actions.deleteThreshold}
              disabled={!selectedMaterialId || !threshold || thresholdActionState.status === "loading"}
            >
              Eliminar umbral
            </button>
            {thresholdActionState.status === "loading" && <p className="muted">Actualizando umbral...</p>}
            {thresholdActionState.status === "error" && <p className="error">{thresholdActionState.message}</p>}
          </form>
        </div>

        <div>
          <h3 className="panel__title">Registrar movimiento</h3>
          <form
            onSubmit={movementForm.handleSubmit(async (values) => {
              if (!selectedMaterialId) return;
              await actions.createMovement(values.type, values.quantity.trim(), values.reason.trim());
              movementForm.reset({ type: "IN", quantity: "", reason: "" });
            })}
            className="formRow"
          >
            <label className="field">
              <span className="field__label">Tipo</span>
              <select disabled={!selectedMaterialId} {...movementForm.register("type")}>
                <option value="IN">Ingreso</option>
                <option value="OUT">Salida</option>
                <option value="ADJUST">Ajuste</option>
              </select>
            </label>
            <label className="field">
              <span className="field__label">Cantidad</span>
              <input
                className={
                  movementForm.formState.errors.quantity ? "field__input field__input--error" : "field__input"
                }
                aria-label="Cantidad movimiento"
                inputMode="decimal"
                disabled={!selectedMaterialId}
                {...movementForm.register("quantity", {
                  required: "Indica la cantidad a mover.",
                  validate: (value) => {
                    const parsed = Number(value.trim());
                    if (!Number.isFinite(parsed) || parsed <= 0) {
                      return "La cantidad debe ser mayor a cero.";
                    }
                    return true;
                  },
                })}
              />
              {movementForm.formState.errors.quantity ? (
                <span className="field__error">{movementForm.formState.errors.quantity.message}</span>
              ) : null}
            </label>
            <label className="field">
              <span className="field__label">Motivo</span>
              <input
                className="field__input"
                aria-label="Motivo movimiento"
                placeholder="Motivo (opcional)"
                disabled={!selectedMaterialId}
                {...movementForm.register("reason")}
              />
            </label>
            <button
              className="button button--primary"
              type="submit"
              disabled={!selectedMaterialId || movementActionState.status === "loading"}
            >
              Registrar
            </button>
            {movementActionState.status === "loading" && <p className="muted">Registrando movimiento...</p>}
            {movementActionState.status === "error" && <p className="error">{movementActionState.message}</p>}
          </form>
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
