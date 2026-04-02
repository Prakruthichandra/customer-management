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
      const mockCustomers = [
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
      ];

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockCustomers,
      });

      const result = await fetchCustomers();

      expect(global.fetch).toHaveBeenCalledWith('/api/v1/customers');
      expect(result).toEqual(mockCustomers);
    });

    it('should handle fetch errors', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
      });

      await expect(fetchCustomers()).rejects.toThrow('Failed to fetch customers: 500 Internal Server Error');
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

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 400,
        statusText: 'Bad Request',
      });

      await expect(createCustomer(newCustomer)).rejects.toThrow('Failed to create customer: 400 Bad Request');
    });
  });
});
