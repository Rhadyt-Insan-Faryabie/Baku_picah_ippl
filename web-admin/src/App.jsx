import { Routes, Route } from 'react-router-dom';
import Login from './pages/Login.jsx';
import BoutVerification from './pages/BoutVerification.jsx';
import MasterData from './pages/MasterData.jsx';
import Moderation from './pages/Moderation.jsx';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/verification" element={<BoutVerification />} />
      <Route path="/master-data" element={<MasterData />} />
      <Route path="/moderation" element={<Moderation />} />
    </Routes>
  );
}
