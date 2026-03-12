import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ContestsList from './ContestsList';
import ContestProblemsGrid from './ContestProblemsGrid';

export default function App() {
  return (
    <div className="min-h-screen bg-gray-100 text-gray-900 font-sans">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<ContestsList />} />
          <Route path="/contests/:contestId/problems" element={<ContestProblemsGrid />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}