import React, { useEffect, useMemo, useReducer, useState } from "react";
import "./App.css";

function App() {
  const apiBaseUrl = "";

  type Material = {
    id: string;
    name: string;
    unit: string;
    createdAt: string;
  };

  type InventoryMovementType = "IN" | "OUT" | "ADJUST";

  type InventoryMovement = {
    id: string;
    materialId: string;
    type: InventoryMovementType;
    quantity: number;
    reason?: string | null;
    createdAt: string;
  };

  type MaterialStock = {
    materialId: string;
    stock: number;
  };

  type MaterialStockThreshold = {
    materialId: string;
    minStock: number;
    updatedAt: string;
  };

  type LowStockAlert = {
    materialId: string;
    name: string;
    unit: string;
    stock: number;
    minStock: number;
  };

  type LoadState =
    | { status: "idle" }
    | { status: "loading" }
    | { status: "loaded" }
    | { status: "error"; message: string };

  type State = {
    materials: Material[];
    materialsState: LoadState;
    movements: InventoryMovement[];
    movementsState: LoadState;
    selectedMaterialId: string | null;
    stock: MaterialStock | null;
    stockState: LoadState;
    threshold: MaterialStockThreshold | null;
    thresholdState: LoadState;
    lowStockAlerts: LowStockAlert[];
    lowStockAlertsState: LoadState;
  };

  type Action =
    | { type: "materials/loading" }
    | { type: "materials/loaded"; materials: Material[] }
    | { type: "materials/error"; message: string }
    | { type: "material/select"; materialId: string | null }
    | { type: "movements/loading" }
    | { type: "movements/loaded"; movements: InventoryMovement[] }
    | { type: "movements/error"; message: string }
    | { type: "stock/loading" }
    | { type: "stock/loaded"; stock: MaterialStock }
    | { type: "stock/error"; message: string }
    | { type: "threshold/loading" }
    | { type: "threshold/loaded"; threshold: MaterialStockThreshold | null }
    | { type: "threshold/error"; message: string }
    | { type: "alerts/loading" }
    | { type: "alerts/loaded"; alerts: LowStockAlert[] }
    | { type: "alerts/error"; message: string };

  const initialState: State = {
    materials: [],
    materialsState: { status: "idle" },
    movements: [],
    movementsState: { status: "idle" },
    selectedMaterialId: null,
    stock: null,
    stockState: { status: "idle" },
    threshold: null,
    thresholdState: { status: "idle" },
    lowStockAlerts: [],
    lowStockAlertsState: { status: "idle" },
  };

  function reducer(state: State, action: Action): State {
    switch (action.type) {
      case "materials/loading":
        return { ...state, materialsState: { status: "loading" } };
      case "materials/loaded":
        return { ...state, materials: action.materials, materialsState: { status: "loaded" } };
      case "materials/error":
        return { ...state, materialsState: { status: "error", message: action.message } };
      case "material/select":
        return {
          ...state,
          selectedMaterialId: action.materialId,
          movements: [],
          movementsState: { status: "idle" },
          stock: null,
          stockState: { status: "idle" },
          threshold: null,
          thresholdState: { status: "idle" },
        };
      case "movements/loading":
        return { ...state, movementsState: { status: "loading" } };
      case "movements/loaded":
        return { ...state, movements: action.movements, movementsState: { status: "loaded" } };
      case "movements/error":
        return { ...state, movementsState: { status: "error", message: action.message } };
      case "stock/loading":
        return { ...state, stockState: { status: "loading" } };
      case "stock/loaded":
        return { ...state, stock: action.stock, stockState: { status: "loaded" } };
      case "stock/error":
        return { ...state, stockState: { status: "error", message: action.message } };
      case "threshold/loading":
        return { ...state, thresholdState: { status: "loading" } };
      case "threshold/loaded":
        return { ...state, threshold: action.threshold, thresholdState: { status: "loaded" } };
      case "threshold/error":
        return { ...state, thresholdState: { status: "error", message: action.message } };
      case "alerts/loading":
        return { ...state, lowStockAlertsState: { status: "loading" } };
      case "alerts/loaded":
        return { ...state, lowStockAlerts: action.alerts, lowStockAlertsState: { status: "loaded" } };
      case "alerts/error":
        return { ...state, lowStockAlertsState: { status: "error", message: action.message } };
    }
  }

  const [state, dispatch] = useReducer(reducer, initialState);
  const [createMaterialName, setCreateMaterialName] = useState("");
  const [createMaterialUnit, setCreateMaterialUnit] = useState("");

  const [createMoveType, setCreateMoveType] = useState<InventoryMovementType>("IN");
  const [createMoveQty, setCreateMoveQty] = useState("1");
  const [createMoveReason, setCreateMoveReason] = useState("");
  const [minStockInput, setMinStockInput] = useState("0");
  const selectedMaterial = useMemo(
    () => state.materials.find((m) => m.id === state.selectedMaterialId) ?? null,
    [state.materials, state.selectedMaterialId],
  );

  async function httpJson<T>(path: string, init?: RequestInit): Promise<T> {
    const res = await fetch(apiBaseUrl + path, {
      ...init,
      headers: {
        "content-type": "application/json",
        ...(init?.headers ?? {}),
      },
    });

    if (!res.ok) {
      let message = `HTTP ${res.status}`;
      try {
        const data = (await res.json()) as unknown;
        const maybeMessage = getApiErrorMessage(data);
        if (maybeMessage) message = maybeMessage;
      } catch {
        // ignore
      }
      throw new Error(message);
    }

    if (res.status === 204) {
      return undefined as T;
    }

    return (await res.json()) as T;
  }

  function getApiErrorMessage(data: unknown): string | null {
    if (!data || typeof data !== "object") return null;
    if (!("message" in data)) return null;
    const msg = (data as Record<string, unknown>).message;
    return typeof msg === "string" && msg.length ? msg : null;
  }

  async function loadMaterials() {
    dispatch({ type: "materials/loading" });
    try {
      const materials = await httpJson<Material[]>("/api/materials");
      dispatch({ type: "materials/loaded", materials });
      if (materials.length > 0 && !state.selectedMaterialId) {
        dispatch({ type: "material/select", materialId: materials[0].id });
      }
    } catch (e) {
      dispatch({ type: "materials/error", message: e instanceof Error ? e.message : "Error" });
    }
  }

  async function loadMovements(materialId: string) {
    dispatch({ type: "movements/loading" });
    try {
      const movements = await httpJson<InventoryMovement[]>(
        `/api/inventory/movements?materialId=${encodeURIComponent(materialId)}`,
      );
      dispatch({ type: "movements/loaded", movements });
    } catch (e) {
      dispatch({ type: "movements/error", message: e instanceof Error ? e.message : "Error" });
    }
  }

  async function loadStock(materialId: string) {
    dispatch({ type: "stock/loading" });
    try {
      const stock = await httpJson<MaterialStock>(`/api/materials/${encodeURIComponent(materialId)}/stock`);
      dispatch({ type: "stock/loaded", stock });
    } catch (e) {
      dispatch({ type: "stock/error", message: e instanceof Error ? e.message : "Error" });
    }
  }

  async function loadThreshold(materialId: string) {
    dispatch({ type: "threshold/loading" });
    try {
      const res = await fetch(`${apiBaseUrl}/api/materials/${encodeURIComponent(materialId)}/threshold`, {
        headers: { "content-type": "application/json" },
      });
      if (res.status === 404) {
        dispatch({ type: "threshold/loaded", threshold: null });
        setMinStockInput("0");
        return;
      }
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}`);
      }
      const threshold = (await res.json()) as MaterialStockThreshold;
      dispatch({ type: "threshold/loaded", threshold });
      setMinStockInput(String(threshold.minStock));
    } catch (e) {
      dispatch({ type: "threshold/error", message: e instanceof Error ? e.message : "Error" });
    }
  }

  async function loadLowStockAlerts() {
    dispatch({ type: "alerts/loading" });
    try {
      const alerts = await httpJson<LowStockAlert[]>("/api/inventory/alerts/low-stock");
      dispatch({ type: "alerts/loaded", alerts });
    } catch (e) {
      dispatch({ type: "alerts/error", message: e instanceof Error ? e.message : "Error" });
    }
  }

  useEffect(() => {
    void loadMaterials();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!state.selectedMaterialId) return;
    void loadMovements(state.selectedMaterialId);
    void loadStock(state.selectedMaterialId);
    void loadThreshold(state.selectedMaterialId);
    void loadLowStockAlerts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [state.selectedMaterialId]);

  async function onCreateMaterial(e: React.FormEvent) {
    e.preventDefault();
    const name = createMaterialName.trim();
    const unit = createMaterialUnit.trim();
    if (!name || !unit) return;
    await httpJson<Material>("/api/materials", { method: "POST", body: JSON.stringify({ name, unit }) });
    setCreateMaterialName("");
    setCreateMaterialUnit("");
    await loadMaterials();
  }

  async function onCreateMovement(e: React.FormEvent) {
    e.preventDefault();
    if (!state.selectedMaterialId) return;

    const qty = Number(createMoveQty);
    if (!Number.isFinite(qty) || qty === 0) return;
    const reason = createMoveReason.trim();

    await httpJson<InventoryMovement>("/api/inventory/movements", {
      method: "POST",
      body: JSON.stringify({
        materialId: state.selectedMaterialId,
        type: createMoveType,
        quantity: qty,
        reason: reason.length ? reason : undefined,
      }),
    });

    setCreateMoveQty("1");
    setCreateMoveReason("");
    await loadMovements(state.selectedMaterialId);
    await loadStock(state.selectedMaterialId);
    await loadLowStockAlerts();
  }

  async function onSaveThreshold(e: React.FormEvent) {
    e.preventDefault();
    if (!state.selectedMaterialId) return;

    const minStock = Number(minStockInput);
    if (!Number.isFinite(minStock) || minStock < 0) return;

    await httpJson<MaterialStockThreshold>(`/api/materials/${encodeURIComponent(state.selectedMaterialId)}/threshold`, {
      method: "PUT",
      body: JSON.stringify({ minStock }),
    });

    await loadThreshold(state.selectedMaterialId);
    await loadLowStockAlerts();
  }

  async function onDeleteThreshold() {
    if (!state.selectedMaterialId) return;
    await httpJson<void>(`/api/materials/${encodeURIComponent(state.selectedMaterialId)}/threshold`, { method: "DELETE" });
    await loadThreshold(state.selectedMaterialId);
    await loadLowStockAlerts();
  }

  return (
    <div style={{ maxWidth: 980, margin: "0 auto", padding: 24 }}>
      <h1>KombaOS</h1>

      <section style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 24, alignItems: "start" }}>
        <div>
          <h2>Materiales</h2>
          <form onSubmit={onCreateMaterial} style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <input
              aria-label="Nombre material"
              placeholder="Nombre"
              value={createMaterialName}
              onChange={(e) => setCreateMaterialName(e.target.value)}
            />
            <input
              aria-label="Unidad material"
              placeholder="Unidad (kg, m, etc)"
              value={createMaterialUnit}
              onChange={(e) => setCreateMaterialUnit(e.target.value)}
            />
            <button type="submit">Crear</button>
          </form>

          {state.materialsState.status === "loading" && <p>Cargando...</p>}
          {state.materialsState.status === "error" && <p>Error: {state.materialsState.message}</p>}

          <ul>
            {state.materials.map((m) => (
              <li key={m.id}>
                <button
                  type="button"
                  onClick={() => dispatch({ type: "material/select", materialId: m.id })}
                  style={{
                    fontWeight: m.id === state.selectedMaterialId ? "bold" : "normal",
                  }}
                >
                  {m.name} ({m.unit})
                </button>
              </li>
            ))}
          </ul>
        </div>

        <div>
          <h2>Inventario</h2>
          <p>Material seleccionado: {selectedMaterial ? `${selectedMaterial.name} (${selectedMaterial.unit})` : "—"}</p>

          <div>
            <strong>Stock:</strong>{" "}
            {state.stockState.status === "loading" && "Cargando..."}
            {state.stockState.status === "error" && `Error: ${state.stockState.message}`}
            {state.stockState.status === "loaded" && state.stock ? state.stock.stock : state.stockState.status === "idle" ? "—" : ""}
          </div>

          <div style={{ marginTop: 8 }}>
            <strong>Alerta stock mínimo:</strong>{" "}
            {state.lowStockAlertsState.status === "loading" && "Cargando..."}
            {state.lowStockAlertsState.status === "error" && `Error: ${state.lowStockAlertsState.message}`}
            {state.lowStockAlertsState.status === "loaded" && state.selectedMaterialId
              ? state.lowStockAlerts.some((a) => a.materialId === state.selectedMaterialId)
                ? "BAJO"
                : "OK"
              : "—"}
          </div>

          <h3>Stock mínimo</h3>
          <form onSubmit={onSaveThreshold} style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <input
              aria-label="Stock mínimo"
              value={minStockInput}
              onChange={(e) => setMinStockInput(e.target.value)}
              inputMode="decimal"
            />
            <button type="submit" disabled={!state.selectedMaterialId}>
              Guardar umbral
            </button>
            <button type="button" onClick={onDeleteThreshold} disabled={!state.selectedMaterialId || !state.threshold}>
              Eliminar umbral
            </button>
          </form>

          <h3>Registrar movimiento</h3>
          <form onSubmit={onCreateMovement} style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
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
            <button type="submit" disabled={!state.selectedMaterialId}>
              Crear movimiento
            </button>
          </form>

          <h3>Movimientos</h3>
          {state.movementsState.status === "loading" && <p>Cargando...</p>}
          {state.movementsState.status === "error" && <p>Error: {state.movementsState.message}</p>}
          <ul>
            {state.movements.map((m) => (
              <li key={m.id}>
                {m.type} {m.quantity} {selectedMaterial?.unit ?? ""} {m.reason ? `— ${m.reason}` : ""}
              </li>
            ))}
          </ul>
        </div>
      </section>
    </div>
  );
}

export default App;
