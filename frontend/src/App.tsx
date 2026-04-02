import React, { useState } from 'react';
import './App.css';
import CustomerForm from './components/CustomerForm';
import CustomerList from './components/CustomerList';
import { Customer } from './types/Customer';

function App() {
  const [refreshKey, setRefreshKey] = useState(0);

  const handleSuccess = (customer: Customer) => {
    setRefreshKey(prev => prev + 1);
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1>Customer Management</h1>
      </header>
      <main className="app-main">
        <section className="app-section">
          <h2>Add Customer</h2>
          <CustomerForm onSuccess={handleSuccess} />
        </section>
        <section className="app-section">
          <h2>Customer List</h2>
          <CustomerList key={refreshKey} />
        </section>
      </main>
    </div>
  );
}

export default App;
