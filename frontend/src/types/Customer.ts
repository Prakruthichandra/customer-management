export interface Customer {
  id: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
}

export interface CustomerRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
}

export interface PagedResponse<T> {
  customers: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  message: string;
}
