import React, {useEffect, useState} from "react";
import {useRouter} from "next/router";


const BackButton = () => {
    const router = useRouter();
    const [email, setEmail] = useState('');

    useEffect(() => {
        setEmail(localStorage.getItem('user-email'))
    }, []);


    const handleClick = async () => {
        if (email === 'admin@gmail.com') {
            await router.replace('/admin-main-page')
        } else {
            await router.push('/user-main-page');
        }
    }


    return (<div className="container mx-auto my-8">
        <div className="h-12">
            <button
                onClick={handleClick}
                className="rounded bg-slate-600 text-white px-6 py-2 font-semibold">
                Back!
            </button>
        </div>
    </div>)
}

export default BackButton;