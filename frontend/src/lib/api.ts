import type {
  InventoryMovement,
  InventoryMovementType,
  LowStockAlert,
  Material,
  MaterialStock,
  MaterialStockThreshold,
  Product,
} from "./types";

const apiBaseUrl = "";

type HttpMethod = "GET" | "POST" | "PUT" | "DELETE";

function getApiErrorMessage(data: unknown): string | null {
  if (!data || typeof data !== "object") return null;
  if (!("message" in data)) return null;
  const msg = (data as Record<string, unknown>).message;
  return typeof msg === "string" && msg.length ? msg : null;
}

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
      message = String(message);
    }
    throw new Error(message);
  }

  if (res.status === 204) {
    return undefined as T;
  }

  return (await res.json()) as T;
}

export type MaterialFilters = {
  q?: string;
  supplier?: string;
  origin?: string;
  certified?: "true" | "false";
};

export type MaterialUpsert = {
  name: string;
  unit: string;
  supplier?: string | null;
  origin?: string | null;
  certified?: boolean;
  costCents?: number | null;
  currency?: string | null;
};

export const materialsApi = {
  async list(filters: MaterialFilters): Promise<Material[]> {
    const params = new URLSearchParams();
    if (filters.q?.trim().length) params.set("q", filters.q.trim());
    if (filters.supplier?.trim().length) params.set("supplier", filters.supplier.trim());
    if (filters.origin?.trim().length) params.set("origin", filters.origin.trim());
    if (filters.certified) params.set("certified", filters.certified);
    const query = params.toString();
    return httpJson<Material[]>(`/api/materials${query ? `?${query}` : ""}`);
  },

  async create(input: MaterialUpsert): Promise<Material> {
    return httpJson<Material>("/api/materials", {
      method: "POST" satisfies HttpMethod,
      body: JSON.stringify(input),
    });
  },

  async update(id: string, input: MaterialUpsert): Promise<Material> {
    return httpJson<Material>(`/api/materials/${encodeURIComponent(id)}`, {
      method: "PUT" satisfies HttpMethod,
      body: JSON.stringify(input),
    });
  },

  async delete(id: string): Promise<void> {
    return httpJson<void>(`/api/materials/${encodeURIComponent(id)}`, {
      method: "DELETE" satisfies HttpMethod,
    });
  },

  async stock(materialId: string): Promise<MaterialStock> {
    return httpJson<MaterialStock>(`/api/materials/${encodeURIComponent(materialId)}/stock`);
  },

  async threshold(materialId: string): Promise<MaterialStockThreshold | null> {
    const res = await fetch(`${apiBaseUrl}/api/materials/${encodeURIComponent(materialId)}/threshold`, {
      headers: { "content-type": "application/json" },
    });

    if (res.status === 404) return null;
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return (await res.json()) as MaterialStockThreshold;
  },

  async upsertThreshold(materialId: string, minStock: number): Promise<MaterialStockThreshold> {
    return httpJson<MaterialStockThreshold>(`/api/materials/${encodeURIComponent(materialId)}/threshold`, {
      method: "PUT" satisfies HttpMethod,
      body: JSON.stringify({ minStock }),
    });
  },

  async deleteThreshold(materialId: string): Promise<void> {
    return httpJson<void>(`/api/materials/${encodeURIComponent(materialId)}/threshold`, {
      method: "DELETE" satisfies HttpMethod,
    });
  },
};

export type InventoryMovementCreate = {
  materialId: string;
  type: InventoryMovementType;
  quantity: number;
  reason?: string;
};

export const inventoryApi = {
  async movements(materialId: string): Promise<InventoryMovement[]> {
    return httpJson<InventoryMovement[]>(`/api/inventory/movements?materialId=${encodeURIComponent(materialId)}`);
  },

  async createMovement(input: InventoryMovementCreate): Promise<InventoryMovement> {
    return httpJson<InventoryMovement>("/api/inventory/movements", {
      method: "POST" satisfies HttpMethod,
      body: JSON.stringify(input),
    });
  },

  async lowStockAlerts(): Promise<LowStockAlert[]> {
    return httpJson<LowStockAlert[]>("/api/inventory/alerts/low-stock");
  },
};

export type ProductUpsert = {
  name: string;
  description: string;
  priceCents: number;
  currency: string;
  active: boolean;
};

export const productsApi = {
  async list(): Promise<Product[]> {
    return httpJson<Product[]>("/api/products");
  },

  async create(input: ProductUpsert): Promise<Product> {
    return httpJson<Product>("/api/products", {
      method: "POST" satisfies HttpMethod,
      body: JSON.stringify(input),
    });
  },

  async update(id: string, input: ProductUpsert): Promise<Product> {
    return httpJson<Product>(`/api/products/${encodeURIComponent(id)}`, {
      method: "PUT" satisfies HttpMethod,
      body: JSON.stringify(input),
    });
  },

  async delete(id: string): Promise<void> {
    return httpJson<void>(`/api/products/${encodeURIComponent(id)}`, {
      method: "DELETE" satisfies HttpMethod,
    });
  },
};

