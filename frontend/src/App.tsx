import { useCallback, useEffect, useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import "./App.css";
import { InventoryPanel } from "./features/inventory/InventoryPanel";
import { MaterialsPanel } from "./features/materials/MaterialsPanel";
import { useMaterials } from "./features/materials/useMaterials";
import { ProductsPanel } from "./features/products/ProductsPanel";
import { healthApi } from "./lib/api";
import type { LoadState } from "./lib/types";

const navItems = [
  { id: "inicio", label: "Inicio", meta: "Resumen" },
  { id: "materiales", label: "Materiales", meta: "Insumos" },
  { id: "inventario", label: "Inventario", meta: "Stock y alertas" },
  { id: "productos", label: "Productos", meta: "Catálogo" },
];

function App() {
  const { materials, materialsState, filtersDraft, setFiltersDraft, actions, createState, editState } = useMaterials();
  const [userSelectedMaterialId, setUserSelectedMaterialId] = useState<string | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userProfile, setUserProfile] = useState({ name: "", organization: "" });
  const [activeSection, setActiveSection] = useState("inicio");
  const [systemStatus, setSystemStatus] = useState<string | null>(null);
  const [systemStatusState, setSystemStatusState] = useState<LoadState>({ status: "idle" });
  const loginForm = useForm<{ name: string; organization: string }>({
    defaultValues: { name: "", organization: "" },
    mode: "onSubmit",
  });

  const loadSystemStatus = useCallback(async () => {
    setSystemStatusState({ status: "loading" });
    try {
      setSystemStatus(await healthApi.status());
      setSystemStatusState({ status: "loaded" });
    } catch (e) {
      setSystemStatus(null);
      setSystemStatusState({ status: "error", message: e instanceof Error ? e.message : "Error" });
    }
  }, []);

  useEffect(() => {
    queueMicrotask(() => {
      void loadSystemStatus();
    });
  }, [loadSystemStatus]);

  const selectedMaterialId = useMemo(() => {
    if (!materials.length) return null;
    if (userSelectedMaterialId && materials.some((m) => m.id === userSelectedMaterialId)) return userSelectedMaterialId;
    return materials[0].id;
  }, [materials, userSelectedMaterialId]);

  const selectedMaterial = useMemo(() => {
    if (!selectedMaterialId) return null;
    return materials.find((m) => m.id === selectedMaterialId) ?? null;
  }, [materials, selectedMaterialId]);

  const materialsSummary = useMemo(() => {
    if (materialsState.status === "loading") return "Cargando...";
    if (materialsState.status === "error") return "Error";
    return `${materials.length} materiales`;
  }, [materials.length, materialsState.status]);

  const systemStatusLabel = useMemo(() => {
    if (systemStatusState.status === "loading") return "Verificando...";
    if (systemStatusState.status === "error") return "Sin conexión";
    if (!systemStatus) return "—";
    return systemStatus.toLowerCase() === "ok" ? "Operativo" : systemStatus;
  }, [systemStatus, systemStatusState.status]);

  const selectedMaterialLabel = selectedMaterial
    ? `${selectedMaterial.name} (${selectedMaterial.unit})`
    : "Sin material seleccionado";

  if (!isAuthenticated) {
    return (
      <div className="app">
        <div className="login">
          <div className="card login__card">
            <div>
              <h1 className="login__title">Bienvenido a KombaOS</h1>
              <p className="login__subtitle">Gestiona inventarios y catálogo sin complicaciones.</p>
            </div>
            <form
              className="form"
              onSubmit={loginForm.handleSubmit((values) => {
                setUserProfile({
                  name: values.name.trim(),
                  organization: values.organization.trim(),
                });
                setIsAuthenticated(true);
              })}
            >
              <label className="field">
                <span className="field__label">Nombre</span>
                <input
                  className={
                    loginForm.formState.errors.name ? "field__input field__input--error" : "field__input"
                  }
                  placeholder="Ej. Ana Pérez"
                  {...loginForm.register("name", {
                    required: "Ingresa tu nombre para continuar.",
                  })}
                />
              </label>
              <label className="field">
                <span className="field__label">Taller o empresa</span>
                <input
                  className="field__input"
                  placeholder="Ej. Taller Andino"
                  {...loginForm.register("organization")}
                />
              </label>
              {loginForm.formState.errors.name ? (
                <p className="field__error">{loginForm.formState.errors.name.message}</p>
              ) : null}
              <button className="button button--primary" type="submit">
                Entrar al sistema
              </button>
              <p className="muted">Este acceso es local y se usa para personalizar la experiencia.</p>
            </form>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="app">
      <div className="shell">
        <aside className="sidebar">
          <div className="brand">
            <p className="brand__title">KombaOS</p>
            <span className="brand__subtitle">Inventario artesanal claro y simple</span>
          </div>
          <nav className="nav">
            {navItems.map((item) => (
              <a
                key={item.id}
                href={`#${item.id}`}
                className={activeSection === item.id ? "nav__link nav__link--active" : "nav__link"}
                onClick={() => setActiveSection(item.id)}
              >
                <span>{item.label}</span>
                <span className="nav__meta">{item.meta}</span>
              </a>
            ))}
          </nav>
          <div className="sidebar__footer">
            <span>Usuario: {userProfile.name || "Operador"}</span>
            <span>{userProfile.organization || "Sin empresa definida"}</span>
          </div>
        </aside>
        <div className="main">
          <div className="topbar">
            <div>
              <h2 className="topbar__title">Hola, {userProfile.name || "operador"}</h2>
              <p className="topbar__subtitle">Revisa el estado del inventario y gestiona tu catálogo.</p>
            </div>
            <div className="topbar__actions">
              <a className="button" href="#materiales" onClick={() => setActiveSection("materiales")}
                >Ir a materiales</a>
              <button
                className="button button--ghost"
                type="button"
                onClick={() => {
                  setIsAuthenticated(false);
                  setUserProfile({ name: "", organization: "" });
                  loginForm.reset({ name: "", organization: "" });
                }}
              >
                Salir
              </button>
            </div>
          </div>

          <main className="content">
            <section id="inicio" className="section">
              <div className="card hero">
                <div>
                  <h1 className="hero__title">Panel principal</h1>
                  <p className="hero__subtitle">Todo lo que necesitas para mantener el control, en un solo lugar.</p>
                </div>
                <div className="cards">
                  <div className="card">
                    <p className="card__title">Materiales registrados</p>
                    <p className="card__value">{materialsSummary}</p>
                  </div>
                  <div className="card">
                    <p className="card__title">Material activo</p>
                    <p className="card__value">{selectedMaterialLabel}</p>
                  </div>
                  <div className="card">
                    <p className="card__title">Estado del sistema</p>
                    <p className="card__value">{systemStatusLabel}</p>
                  </div>
                </div>
                <div className="hero__actions">
                  <a className="button button--primary" href="#materiales" onClick={() => setActiveSection("materiales")}
                    >Crear material</a>
                  <a className="button" href="#inventario" onClick={() => setActiveSection("inventario")}
                    >Registrar movimiento</a>
                  <a className="button" href="#productos" onClick={() => setActiveSection("productos")}
                    >Agregar producto</a>
                </div>
              </div>
            </section>

            <section id="materiales" className="section">
              <div>
                <h2 className="section__title">Materiales</h2>
                <p className="section__subtitle">Registra insumos, proveedores y certificaciones.</p>
              </div>
              <MaterialsPanel
                materials={materials}
                materialsState={materialsState}
                createState={createState}
                editState={editState}
                filtersDraft={filtersDraft}
                setFiltersDraft={setFiltersDraft}
                actions={actions}
                selectedMaterialId={selectedMaterialId}
                selectedMaterial={selectedMaterial}
                onSelectMaterialId={setUserSelectedMaterialId}
              />
            </section>

            <section id="inventario" className="section">
              <div>
                <h2 className="section__title">Inventario</h2>
                <p className="section__subtitle">Controla stock, alertas y movimientos.</p>
              </div>
              <InventoryPanel selectedMaterial={selectedMaterial} selectedMaterialId={selectedMaterialId} />
            </section>

            <section id="productos" className="section">
              <div>
                <h2 className="section__title">Productos</h2>
                <p className="section__subtitle">Mantén tu catálogo actualizado y con precios claros.</p>
              </div>
              <ProductsPanel />
            </section>
          </main>
        </div>
      </div>
    </div>
  );
}

export default App;
