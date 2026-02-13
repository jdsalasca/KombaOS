export type Material = {
  id: string;
  name: string;
  unit: string;
  supplier?: string | null;
  origin?: string | null;
  certified: boolean;
  costCents?: number | null;
  currency?: string | null;
  createdAt: string;
};

export type Product = {
  id: string;
  name: string;
  description: string;
  priceCents: number;
  currency: string;
  active: boolean;
  createdAt: string;
};

export type InventoryMovementType = "IN" | "OUT" | "ADJUST";

export type InventoryMovement = {
  id: string;
  materialId: string;
  type: InventoryMovementType;
  quantity: number;
  reason?: string | null;
  createdAt: string;
};

export type MaterialStock = {
  materialId: string;
  stock: number;
};

export type MaterialStockThreshold = {
  materialId: string;
  minStock: number;
  updatedAt: string;
};

export type LowStockAlert = {
  materialId: string;
  name: string;
  unit: string;
  stock: number;
  minStock: number;
};

export type LoadState =
  | { status: "idle" }
  | { status: "loading" }
  | { status: "loaded" }
  | { status: "error"; message: string };

export function parseCopInputToCents(input: string): number | null {
  const trimmed = input.trim();
  if (!trimmed.length) return null;

  if (/[.,]\d{1,2}$/.test(trimmed)) return null;

  const normalized = trimmed.replace(/\s+/g, "").replace(/[.,]/g, "");
  if (!/^\d+$/.test(normalized)) return null;

  const pesos = Number.parseInt(normalized, 10);
  if (!Number.isSafeInteger(pesos) || pesos < 0) return null;

  const cents = pesos * 100;
  if (!Number.isSafeInteger(cents)) return null;
  return cents;
}

export function formatCopFromCents(cents: number | null | undefined): string | null {
  if (cents == null) return null;

  const pesos = cents / 100;
  if (!Number.isFinite(pesos)) return null;

  return new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
    maximumFractionDigits: 0,
  }).format(pesos);
}
