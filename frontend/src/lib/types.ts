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

