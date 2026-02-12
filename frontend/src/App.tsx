import { useMemo, useState } from "react";
import "./App.css";
import { InventoryPanel } from "./features/inventory/InventoryPanel";
import { MaterialsPanel } from "./features/materials/MaterialsPanel";
import { useMaterials } from "./features/materials/useMaterials";
import { ProductsPanel } from "./features/products/ProductsPanel";

function App() {
  const { materials, materialsState, filtersDraft, setFiltersDraft, actions } = useMaterials();
  const [userSelectedMaterialId, setUserSelectedMaterialId] = useState<string | null>(null);

  const selectedMaterialId = useMemo(() => {
    if (!materials.length) return null;
    if (userSelectedMaterialId && materials.some((m) => m.id === userSelectedMaterialId)) return userSelectedMaterialId;
    return materials[0].id;
  }, [materials, userSelectedMaterialId]);

  const selectedMaterial = useMemo(() => {
    if (!selectedMaterialId) return null;
    return materials.find((m) => m.id === selectedMaterialId) ?? null;
  }, [materials, selectedMaterialId]);

  return (
    <div className="app">
      <header className="app__header">
        <h1 className="app__title">KombaOS</h1>
      </header>

      <main className="app__main">
        <section className="grid">
          <MaterialsPanel
            materials={materials}
            materialsState={materialsState}
            filtersDraft={filtersDraft}
            setFiltersDraft={setFiltersDraft}
            actions={actions}
            selectedMaterialId={selectedMaterialId}
            selectedMaterial={selectedMaterial}
            onSelectMaterialId={setUserSelectedMaterialId}
          />
          <InventoryPanel selectedMaterial={selectedMaterial} selectedMaterialId={selectedMaterialId} />
          <ProductsPanel />
        </section>
      </main>
    </div>
  );
}

export default App;
