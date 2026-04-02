import { fetchCustomers, createCustomer } from './customerApi';

describe('customerApi', () => {
  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    jest.resetAllMocks();
  });

  describe('fetchCustomers', () => {
    it('should fetch all customers', async () => {
      const mockPagedResponse = {
        customers: [
          {
            id: '123e4567-e89b-12d3-a456-426614174000',
            firstName: 'John',
            lastName: 'Doe',
            dateOfBirth: '1990-01-15',
          },
          {
            id: '123e4567-e89b-12d3-a456-426614174001',
            firstName: 'Jane',
            lastName: 'Smith',
            dateOfBirth: '1985-05-20',
          },
        ],
        page: 0,
        size: 20,
        totalElements: 2,
        totalPages: 1,
      };

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockPagedResponse,
      });

      const result = await fetchCustomers();

      expect(global.fetch).toHaveBeenCalledWith('/api/v1/customers');
      expect(result).toEqual(mockPagedResponse);
    });

    it('should handle fetch errors', async () => {
      const mockError = {
        timestamp: '2026-04-02T10:00:00',
        status: 500,
        message: 'Internal Server Error',
      };

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        json: async () => mockError,
      });

      await expect(fetchCustomers()).rejects.toThrow('Internal Server Error');
    });
  });

  describe('createCustomer', () => {
    it('should create a customer', async () => {
      const newCustomer = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '1990-01-15',
      };

      const createdCustomer = {
        id: '123e4567-e89b-12d3-a456-426614174000',
        ...newCustomer,
      };

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => createdCustomer,
      });

      const result = await createCustomer(newCustomer);

      expect(global.fetch).toHaveBeenCalledWith('/api/v1/customers', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newCustomer),
      });
      expect(result).toEqual(createdCustomer);
    });

    it('should handle create errors', async () => {
      const newCustomer = {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '2030-01-15',
      };

      const mockError = {
        timestamp: '2026-04-02T10:00:00',
        status: 400,
        message: 'Date of birth must be in the past',
      };

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        json: async () => mockError,
      });

      await expect(createCustomer(newCustomer)).rejects.toThrow('Date of birth must be in the past');
    });
  });
});
