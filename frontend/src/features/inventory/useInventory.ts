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
    } catch (e) {
      setStockState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, [selectedMaterialId]);

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
    } catch (e) {
      setThresholdState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, [selectedMaterialId]);

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
    } catch (e) {
      setMovementsState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, [selectedMaterialId]);

  const loadAlerts = useCallback(async () => {
    setLowStockAlertsState({ status: "loading" });
    try {
      setLowStockAlerts(await inventoryApi.lowStockAlerts());
      setLowStockAlertsState({ status: "loaded" });
    } catch (e) {
      setLowStockAlertsState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, []);

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
        await materialsApi.upsertThreshold(selectedMaterialId, parsed);
        await Promise.all([loadThreshold(), loadAlerts()]);
      },
      async deleteThreshold() {
        if (!selectedMaterialId) return;
        await materialsApi.deleteThreshold(selectedMaterialId);
        await Promise.all([loadThreshold(), loadAlerts()]);
      },
      async createMovement(type: InventoryMovementType, qtyRaw: string, reason: string) {
        if (!selectedMaterialId) return;
        const qtyTrimmed = qtyRaw.trim();
        if (!qtyTrimmed.length) return;
        const quantity = Number(qtyTrimmed);
        if (!Number.isFinite(quantity) || quantity <= 0) return;

        await inventoryApi.createMovement({
          materialId: selectedMaterialId,
          type,
          quantity,
          reason: reason.trim().length ? reason.trim() : undefined,
        });

        await Promise.all([loadMovements(), loadStock(), loadAlerts()]);
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
    actions,
  };
}
