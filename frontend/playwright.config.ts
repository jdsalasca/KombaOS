import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./e2e",
  timeout: 90_000,
  expect: { timeout: 15_000 },
  use: {
    baseURL: "http://localhost:8081",
    trace: "retain-on-failure",
    screenshot: "only-on-failure",
    video: "retain-on-failure",
  },
  webServer: {
    command:
      "pwsh -NoProfile -File e2e/start-fullstack.ps1",
    url: "http://localhost:8081/actuator/health",
    timeout: 180_000,
    reuseExistingServer: false,
  },
});
