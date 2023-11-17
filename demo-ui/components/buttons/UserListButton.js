import React from "react";
import {useRouter} from "next/router";


const UserListButton = () => {
    const router = useRouter();
    const handleClick = async () => {
        await router.push('/user-list-page');
    }

    return (<div className="container mx-auto my-8">
        <div className="h-12">
            <button
                onClick={handleClick}
                className="rounded bg-slate-600 text-white px-6 py-2 font-semibold">
                User list
            </button>
        </div>
    </div>)
}

export default UserListButton;