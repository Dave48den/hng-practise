import { useNavigate } from "react-router-dom";

function Dashboard() {

    const navigate = useNavigate();

    const logout = () => {

        localStorage.removeItem("token");

        navigate("/");
    };

    return (

        <div style={{ padding: "20px" }}>

            <h1>Dashboard</h1>

            <p>Welcome to Insighta Labs+</p>

            <button onClick={logout}>
                Logout
            </button>

        </div>
    );
}

export default Dashboard;