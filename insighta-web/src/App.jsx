import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";
import Account from "./pages/Account.jsx";

import Login from "./pages/Login";
import GithubCallback from "./pages/GithubCallback";
import Dashboard from "./pages/Dashboard";
import Profiles from "./pages/Profiles";

function App() {

    return (

        <BrowserRouter>

            <Routes>

                <Route path="/" element={<Login />} />
                <Route
                    path="/profiles"
                    element={
                        <ProtectedRoute>
                            <Profiles />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/account"
                    element={
                        <ProtectedRoute>
                            <Account />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/auth/github/callback"
                    element={<GithubCallback />}
                />

                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    }
                />

            </Routes>

        </BrowserRouter>
    );
}

export default App;