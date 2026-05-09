function Account() {

    const token = localStorage.getItem("token");

    return (

        <div style={{ padding: "20px" }}>

            <h1>Account</h1>

            <p>
                Authentication Status:
                {
                    token
                        ? " Logged In"
                        : " Not Logged In"
                }
            </p>

            <p>
                Token Preview:
            </p>

            <textarea
                value={token || ""}
                readOnly
                rows="10"
                cols="80"
            />

        </div>
    );
}

export default Account;