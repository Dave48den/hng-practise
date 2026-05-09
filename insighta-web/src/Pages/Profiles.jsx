import { useEffect, useState } from "react";
import axios from "axios";
import API_URL from "../config";

const Profiles = () => {

    const [profiles, setProfiles] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const token = localStorage.getItem("token");

        axios.get(
            `${API_URL}/api/profiles`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "X-API-Version": "1"
                }
            }
        )
            .then((res) => {

                setProfiles(res.data.data);

                setLoading(false);
            })
            .catch((err) => {

                console.log(err);

                setLoading(false);
            });

    }, []);

    if (loading) {
        return <h2>Loading profiles...</h2>;
    }

    return (

        <div style={{ padding: "20px" }}>

            <h1>Profiles</h1>

            {
                profiles.map((profile) => (

                    <div
                        key={profile.id}
                        style={{
                            border: "1px solid gray",
                            padding: "10px",
                            marginBottom: "10px"
                        }}
                    >

                        <h3>{profile.name}</h3>

                        <p>Gender: {profile.gender}</p>

                        <p>Age: {profile.age}</p>

                        <p>Country: {profile.countryName}</p>

                    </div>
                ))
            }

        </div>
    );
};

export default Profiles;