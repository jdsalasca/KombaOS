import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [
    {
      name: "mock-assets",
      enforce: "pre",
      resolveId(id) {
        if (id.endsWith(".svg") || id.endsWith(".css") || id.includes("vite.svg")) {
          return "\0mock-asset:" + id;
        }
      },
      load(id) {
        if (!id.startsWith("\0mock-asset:")) return;
        if (id.includes(".svg")) {
          return 'export default "data:image/svg+xml,<svg xmlns=\\"http://www.w3.org/2000/svg\\"></svg>"';
        }
        if (id.includes(".css")) return "";
      },
    },
    react(),
  ],
  test: {
    environment: "jsdom",
    setupFiles: ["./src/test/setup.ts"],
  },
});
