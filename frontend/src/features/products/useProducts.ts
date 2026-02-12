import { useCallback, useEffect, useMemo, useState } from "react";
import { productsApi } from "../../lib/api";
import { parseCopInputToCents } from "../../lib/types";
import type { LoadState, Product } from "../../lib/types";

export function useProducts() {
  const [products, setProducts] = useState<Product[]>([]);
  const [productsState, setProductsState] = useState<LoadState>({ status: "idle" });
  const [userSelectedProductId, setUserSelectedProductId] = useState<string | null>(null);

  const list = useCallback(async () => {
    setProductsState({ status: "loading" });
    try {
      setProducts(await productsApi.list());
      setProductsState({ status: "loaded" });
    } catch (e) {
      setProductsState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, []);

  useEffect(() => {
    queueMicrotask(() => {
      void list();
    });
  }, [list]);

  const selectedProductId = useMemo(() => {
    if (!products.length) return null;
    if (userSelectedProductId && products.some((p) => p.id === userSelectedProductId)) return userSelectedProductId;
    return products[0].id;
  }, [products, userSelectedProductId]);

  const selectedProduct = useMemo(() => {
    if (!selectedProductId) return null;
    return products.find((p) => p.id === selectedProductId) ?? null;
  }, [products, selectedProductId]);

  const currency = "COP";

  const actions = useMemo(
    () => ({
      reload: list,
      async create(input: {
        name: string;
        description: string;
        priceCop: string;
        active: boolean;
      }) {
        const name = input.name.trim();
        if (!name.length) return;

        const description = input.description.trim();
        if (!description.length) return;

        const priceCents = parseCopInputToCents(input.priceCop);
        if (priceCents == null) return;

        await productsApi.create({
          name,
          description,
          priceCents,
          currency,
          active: input.active,
        });
        await list();
      },

      async update(productId: string, input: {
        name: string;
        description: string;
        priceCop: string;
        active: boolean;
      }) {
        const name = input.name.trim();
        if (!name.length) return;

        const description = input.description.trim();
        if (!description.length) return;

        const priceCents = parseCopInputToCents(input.priceCop);
        if (priceCents == null) return;

        await productsApi.update(productId, {
          name,
          description,
          priceCents,
          currency,
          active: input.active,
        });
        await list();
      },

      async remove(productId: string) {
        await productsApi.delete(productId);
        await list();
      },
    }),
    [list],
  );

  return {
    products,
    productsState,
    selectedProductId,
    setSelectedProductId: setUserSelectedProductId,
    selectedProduct,
    actions,
  };
}
