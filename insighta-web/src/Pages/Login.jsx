const Login = () => {

    const handleLogin = () => {

        const clientId = "Ov23libTKVOGGXySyEwe";

        const redirectUri =
            `${window.location.origin}/auth/github/callback`;

        window.location.href =
            `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&scope=user:email`;
    };

    return (
        <div style={{ padding: "40px" }}>

            <h1>Insighta Labs</h1>

            <button onClick={handleLogin}>
                Continue with GitHub
            </button>

        </div>
    );
};

export default Login;