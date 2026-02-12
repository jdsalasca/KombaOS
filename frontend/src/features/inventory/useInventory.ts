import { useCallback, useEffect, useMemo, useState } from "react";
import { inventoryApi, materialsApi } from "../../lib/api";
import type { InventoryMovement, InventoryMovementType, LoadState, LowStockAlert, MaterialStock, MaterialStockThreshold } from "../../lib/types";

export function useInventory(selectedMaterialId: string | null) {
  const [stock, setStock] = useState<MaterialStock | null>(null);
  const [stockState, setStockState] = useState<LoadState>({ status: "idle" });

  const [threshold, setThreshold] = useState<MaterialStockThreshold | null>(null);
  const [thresholdState, setThresholdState] = useState<LoadState>({ status: "idle" });

  const [movements, setMovements] = useState<InventoryMovement[]>([]);
  const [movementsState, setMovementsState] = useState<LoadState>({ status: "idle" });

  const [lowStockAlerts, setLowStockAlerts] = useState<LowStockAlert[]>([]);
  const [lowStockAlertsState, setLowStockAlertsState] = useState<LoadState>({ status: "idle" });
  const [thresholdActionState, setThresholdActionState] = useState<LoadState>({ status: "idle" });
  const [movementActionState, setMovementActionState] = useState<LoadState>({ status: "idle" });
  const [lastUpdatedAt, setLastUpdatedAt] = useState<Date | null>(null);

  const markUpdated = useCallback(() => {
    setLastUpdatedAt(new Date());
  }, []);

  const loadStock = useCallback(async () => {
    if (!selectedMaterialId) {
      setStock(null);
      setStockState({ status: "idle" });
      return;
    }
    setStockState({ status: "loading" });
    try {
      setStock(await materialsApi.stock(selectedMaterialId));
      setStockState({ status: "loaded" });
      markUpdated();
    } catch (e) {
      setStockState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, [markUpdated, selectedMaterialId]);

  const loadThreshold = useCallback(async () => {
    if (!selectedMaterialId) {
      setThreshold(null);
      setThresholdState({ status: "idle" });
      return;
    }
    setThresholdState({ status: "loading" });
    try {
      setThreshold(await materialsApi.threshold(selectedMaterialId));
      setThresholdState({ status: "loaded" });
      markUpdated();
    } catch (e) {
      setThresholdState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, [markUpdated, selectedMaterialId]);

  const loadMovements = useCallback(async () => {
    if (!selectedMaterialId) {
      setMovements([]);
      setMovementsState({ status: "idle" });
      return;
    }
    setMovementsState({ status: "loading" });
    try {
      setMovements(await inventoryApi.movements(selectedMaterialId));
      setMovementsState({ status: "loaded" });
      markUpdated();
    } catch (e) {
      setMovementsState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, [markUpdated, selectedMaterialId]);

  const loadAlerts = useCallback(async () => {
    setLowStockAlertsState({ status: "loading" });
    try {
      setLowStockAlerts(await inventoryApi.lowStockAlerts());
      setLowStockAlertsState({ status: "loaded" });
      markUpdated();
    } catch (e) {
      setLowStockAlertsState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, [markUpdated]);

  useEffect(() => {
    queueMicrotask(() => {
      void loadStock();
      void loadThreshold();
      void loadMovements();
      void loadAlerts();
    });
  }, [loadAlerts, loadMovements, loadStock, loadThreshold]);

  const actions = useMemo(
    () => ({
      reloadAll: async () => {
        await Promise.all([loadStock(), loadThreshold(), loadMovements(), loadAlerts()]);
      },
      async saveThreshold(minStockRaw: string) {
        if (!selectedMaterialId) return;
        const trimmed = minStockRaw.trim();
        if (!trimmed.length) return;
        const parsed = Number(trimmed);
        if (!Number.isFinite(parsed) || parsed < 0) return;
        setThresholdActionState({ status: "loading" });
        try {
          await materialsApi.upsertThreshold(selectedMaterialId, parsed);
          await Promise.all([loadThreshold(), loadAlerts()]);
          setThresholdActionState({ status: "loaded" });
        } catch (e) {
          setThresholdActionState({ status: "error", message: e instanceof Error ? e.message : "Error" });
        }
      },
      async deleteThreshold() {
        if (!selectedMaterialId) return;
        setThresholdActionState({ status: "loading" });
        try {
          await materialsApi.deleteThreshold(selectedMaterialId);
          await Promise.all([loadThreshold(), loadAlerts()]);
          setThresholdActionState({ status: "loaded" });
        } catch (e) {
          setThresholdActionState({ status: "error", message: e instanceof Error ? e.message : "Error" });
        }
      },
      async createMovement(type: InventoryMovementType, qtyRaw: string, reason: string) {
        if (!selectedMaterialId) return;
        const qtyTrimmed = qtyRaw.trim();
        if (!qtyTrimmed.length) return;
        const quantity = Number(qtyTrimmed);
        if (!Number.isFinite(quantity) || quantity <= 0) return;

        setMovementActionState({ status: "loading" });
        try {
          await inventoryApi.createMovement({
            materialId: selectedMaterialId,
            type,
            quantity,
            reason: reason.trim().length ? reason.trim() : undefined,
          });

          await Promise.all([loadMovements(), loadStock(), loadAlerts()]);
          setMovementActionState({ status: "loaded" });
        } catch (e) {
          setMovementActionState({ status: "error", message: e instanceof Error ? e.message : "Error" });
        }
      },
    }),
    [loadAlerts, loadMovements, loadStock, loadThreshold, selectedMaterialId],
  );

  return {
    stock,
    stockState,
    threshold,
    thresholdState,
    movements,
    movementsState,
    lowStockAlerts,
    lowStockAlertsState,
    thresholdActionState,
    movementActionState,
    actions,
    lastUpdatedAt,
  };
}
