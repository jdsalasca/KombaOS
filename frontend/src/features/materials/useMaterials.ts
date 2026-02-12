import { useCallback, useEffect, useMemo, useState } from "react";
import { materialsApi } from "../../lib/api";
import { parseCopInputToCents } from "../../lib/types";
import type { LoadState, Material } from "../../lib/types";

export type CertifiedFilter = "" | "true" | "false";

export type MaterialsFiltersState = {
  q: string;
  supplier: string;
  origin: string;
  certified: CertifiedFilter;
};

export function useMaterials() {
  const [materials, setMaterials] = useState<Material[]>([]);
  const [materialsState, setMaterialsState] = useState<LoadState>({ status: "idle" });

  const [filtersDraft, setFiltersDraft] = useState<MaterialsFiltersState>({
    q: "",
    supplier: "",
    origin: "",
    certified: "",
  });
  const [filtersApplied, setFiltersApplied] = useState<MaterialsFiltersState>(filtersDraft);

  const list = useCallback(async () => {
    setMaterialsState({ status: "loading" });
    try {
      const next = await materialsApi.list({
        q: filtersApplied.q,
        supplier: filtersApplied.supplier,
        origin: filtersApplied.origin,
        certified: filtersApplied.certified || undefined,
      });
      setMaterials(next);
      setMaterialsState({ status: "loaded" });
    } catch (e) {
      setMaterialsState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, [filtersApplied]);

  useEffect(() => {
    queueMicrotask(() => {
      void list();
    });
  }, [list]);

  const actions = useMemo(
    () => ({
      applyFilters() {
        setFiltersApplied(filtersDraft);
      },
      clearFilters() {
        const cleared: MaterialsFiltersState = { q: "", supplier: "", origin: "", certified: "" };
        setFiltersDraft(cleared);
        setFiltersApplied(cleared);
      },
      async create(input: {
        name: string;
        unit: string;
        supplier: string;
        origin: string;
        certified: boolean;
        costCop: string;
      }) {
        const name = input.name.trim();
        const unit = input.unit.trim();
        if (!name || !unit) return;

        const supplier = input.supplier.trim();
        const origin = input.origin.trim();

        const costCents = input.costCop.trim().length ? parseCopInputToCents(input.costCop) : null;
        if (input.costCop.trim().length && costCents == null) return;
        const currency = costCents == null ? null : "COP";

        await materialsApi.create({
          name,
          unit,
          supplier: supplier.length ? supplier : null,
          origin: origin.length ? origin : null,
          certified: input.certified,
          costCents,
          currency,
        });
        await list();
      },
      async update(materialId: string, input: {
        name: string;
        unit: string;
        supplier: string;
        origin: string;
        certified: boolean;
        costCop: string;
      }) {
        const name = input.name.trim();
        const unit = input.unit.trim();
        if (!name || !unit) return;

        const supplier = input.supplier.trim();
        const origin = input.origin.trim();

        const costCents = input.costCop.trim().length ? parseCopInputToCents(input.costCop) : null;
        if (input.costCop.trim().length && costCents == null) return;
        const currency = costCents == null ? null : "COP";

        await materialsApi.update(materialId, {
          name,
          unit,
          supplier: supplier.length ? supplier : null,
          origin: origin.length ? origin : null,
          certified: input.certified,
          costCents,
          currency,
        });
        await list();
      },
      async remove(materialId: string) {
        await materialsApi.delete(materialId);
        await list();
      },
      reload: list,
    }),
    [filtersDraft, list],
  );

  return {
    materials,
    materialsState,
    filtersDraft,
    setFiltersDraft,
    actions,
  };
}
