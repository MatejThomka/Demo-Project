import {useState, useEffect, React} from "react";
import User from "./User";

const UserList = () => {
    const USER_API_BASE_URL = "http://localhost:8080/api/users"
    const [users, setUsers] = useState(null);
    const [loading, setLoading] = useState(true);
    useEffect(() => {
        setLoading(true);
        try {
            fetch(USER_API_BASE_URL, {
                method: "GET", headers: {
                    Accept: 'application/json', Authorization: 'Bearer ' + localStorage.getItem('token'),
                }
            })
                .then((res) => res.json())
                .then((data) => {
                    setUsers(data);
                    setLoading(false);
                })
        } catch (exception) {
            console.log(exception)
        }

    }, []);

    return (<div className="container mx-auto my-8">
        <div className="flex shadow border-b">
            <table className="min-w-full">
                <thead className="bg-gray-50">
                <tr>
                    <th className="text-left font-medium text-gray-500 uppercase tracking-wide py-3 px-6">First
                        Name
                    </th>
                    <th className="text-left font-medium text-gray-500 uppercase tracking-wide py-3 px-6">Last
                        Name
                    </th>
                    <th className="text-left font-medium text-gray-500 uppercase tracking-wide py-3 px-6">E-mail</th>
                    <th className="text-right font-medium text-gray-500 uppercase tracking-wide py-3 px-6">Actions</th>
                </tr>
                </thead>
                {!loading && (<tbody className="bg-white">
                {users?.map((user) => (<User user={user} key={user.id}/>))}
                </tbody>)}
            </table>
        </div>
    </div>);
};

export default UserList;