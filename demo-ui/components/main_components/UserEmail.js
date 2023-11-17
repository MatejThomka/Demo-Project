import {useEffect, useState} from "react";

const UserEmail = () => {
    const [email, setEmail] = useState('');

    useEffect(() => {
        setEmail(localStorage.getItem('user-email'))
    }, []);


    return (<div className="container mx-auto my-8">
        <div className="h-12">
            <p className="rounded bg-slate-600 text-white px-6 py-2 font-semibold">
                {email}
            </p>
        </div>
    </div>)
}

export default UserEmail;