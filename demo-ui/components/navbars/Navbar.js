import React from 'react'
import SignIn from "../buttons/SignIn";

const Navbar = () => {

    return (<div className="bg-gray-800">
        <div className="h-16 px-8 flex items-center">
            <p className="text-white font-bold flex-auto">
                Demo of application
            </p>
            <div className="flex px-2">
                <SignIn/>
            </div>
        </div>
    </div>);
};

export default Navbar;