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
