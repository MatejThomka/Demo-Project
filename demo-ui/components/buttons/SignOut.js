import SignIn from "./SignIn";
import {useRouter} from "next/router";


const SignOut = () => {

    const router = useRouter()
    async function deleteToken() {
        if (SignIn) {
            localStorage.clear();
            await router.push('/')
        }
    }

    return (<div className="container mx-auto my-8">
        <div className="h-12">
            <button onClick={deleteToken} className="rounded bg-slate-600 text-white px-6 py-2 font-semibold">
                Sign out!
            </button>
        </div>
    </div>);
};

export default SignOut;