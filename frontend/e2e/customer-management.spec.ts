import { test, expect } from '@playwright/test';

test.describe('Customer Management E2E Tests', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // Wait for app to load - either table or empty state
    await page.waitForLoadState('networkidle');
  });

  test('should create a new customer and display in list', async ({ page }) => {
    // Count rows before adding
    const initialRowCount = await page.locator('.customer-table tbody tr').count().catch(() => 0);

    // Fill form
    await page.getByLabel(/first name/i).fill('TestUser');
    await page.getByLabel(/last name/i).fill('Chandra');
    await page.getByLabel(/date of birth/i).fill('1990-01-15');

    // Submit
    await page.getByRole('button', { name: /create customer/i }).click();

    // Wait for form to clear
    await expect(page.getByLabel(/first name/i)).toHaveValue('');

    // Simple approach: wait for row count to increase
    await expect(async () => {
      const newRowCount = await page.locator('.customer-table tbody tr').count();
      expect(newRowCount).toBeGreaterThan(initialRowCount);
    }).toPass({ timeout: 10000 });

    // Verify the customer exists in the table
    const tableText = await page.locator('.customer-table').textContent();
    expect(tableText).toContain('TestUser');
    expect(tableText).toContain('Chandra');
  });

  test('should show validation errors for empty form', async ({ page }) => {
    // Click submit without filling form
    await page.getByRole('button', { name: /create customer/i }).click();

    // Check for validation errors
    await expect(page.getByText(/first name is required/i)).toBeVisible();
    await expect(page.getByText(/last name is required/i)).toBeVisible();
    await expect(page.getByText(/date of birth is required/i)).toBeVisible();
  });

  test('should show validation error for future date', async ({ page }) => {
    await page.getByLabel(/first name/i).fill('Future');
    await page.getByLabel(/last name/i).fill('Person');
    await page.getByLabel(/date of birth/i).fill('2030-12-31');

    await page.getByRole('button', { name: /create customer/i }).click();

    await expect(page.getByText(/must be in the past/i)).toBeVisible();
  });

  test('should show validation error for invalid characters', async ({ page }) => {
    await page.getByLabel(/first name/i).fill('Test123');
    await page.getByLabel(/last name/i).fill('User');
    await page.getByLabel(/date of birth/i).fill('1990-01-01');

    await page.getByRole('button', { name: /create customer/i }).click();

    await expect(page.getByText(/invalid characters/i)).toBeVisible();
  });

  test('should sort customers by column headers', async ({ page }) => {
    // Check if we can see the table (skip if no data)
    const hasTable = await page.locator('.customer-table').isVisible().catch(() => false);

    if (!hasTable) {
      // Create some test data first
      const testData = [
        { firstName: 'Alice', lastName: 'Zulu', dob: '1990-01-01' },
        { firstName: 'Zoe', lastName: 'Alpha', dob: '1991-02-02' },
      ];

      for (const data of testData) {
        await page.getByLabel(/first name/i).fill(data.firstName);
        await page.getByLabel(/last name/i).fill(data.lastName);
        await page.getByLabel(/date of birth/i).fill(data.dob);
        await page.getByRole('button', { name: /create customer/i }).click();
        await page.waitForTimeout(1000);
      }
    }

    // Wait for table to be visible
    await expect(page.locator('.customer-table')).toBeVisible({ timeout: 5000 });

    // Get first row before sorting
    const initialFirstName = await page.locator('.customer-table tbody tr:first-child td:nth-child(2)').textContent();

    // Click on First Name header to sort
    await page.locator('th.sortable', { hasText: 'First Name' }).click();
    await page.waitForTimeout(1000);

    // First row should have changed (or stayed if already sorted)
    const afterSortFirstName = await page.locator('.customer-table tbody tr:first-child td:nth-child(2)').textContent();

    // Click again to reverse sort
    await page.locator('th.sortable', { hasText: 'First Name' }).click();
    await page.waitForTimeout(1000);

    const afterReverseSortFirstName = await page.locator('.customer-table tbody tr:first-child td:nth-child(2)').textContent();

    // After reversing, it should be different from the previous sort
    expect(afterReverseSortFirstName).not.toBe(afterSortFirstName);
  });

});
