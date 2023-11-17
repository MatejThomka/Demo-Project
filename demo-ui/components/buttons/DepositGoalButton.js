import React, {useState} from "react";
import {Dialog, Transition} from "@headlessui/react";
import {Fragment} from "react";
import {error} from "next/dist/build/output/log";
import axios from "axios";
import API_URLS_CONFIG from "../../API_URLS_CONFIG";



const DepositGoalButton = ({ refresh }) => {
    const GOAL_API_DEPOSIT_URL = API_URLS_CONFIG.BASE_URL + "/api/goal/deposit";
    const [isOpen, setIsOpen] = useState(false);
    const [deposit, setDeposit] = useState(null);
    const [nameOfGoal, setNameOfGoal] = useState(null);
    const [message, setMessage] = useState('');
    const [successful, setSuccessful] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [unsuccessful, setUnsuccessful] = useState(false);

    function closeModal() {
        setIsOpen(false);
        setNameOfGoal(null);
        setDeposit(null);
    }

    function openModal() {
        setIsOpen(true);
        setUnsuccessful(false)
        setSuccessful(false)
    }

    const handleNameOfGoalChange = (event) => {
        if (event.target.value === '') {
            setNameOfGoal(null);
        } else {
            setNameOfGoal(event.target.value);
        }

    }

    const handleDepositChange = (event) => {
        if (event.target.value === '') {
            setDeposit(null);
        } else {
            setDeposit(event.target.value);
        }

    }

    const addGoal = async () => {
        try {
            const response = await axios({method:'post', url: GOAL_API_DEPOSIT_URL, data: {name: nameOfGoal, deposit: deposit},
                headers: {
                    Accept: 'application/json', Authorization: 'Bearer ' + localStorage.getItem('token'),
                },
            });
            if (response.status === 200) {
                setSuccessful(true)
                setMessage("Money was successfully added to your Financial Goal!")
                refresh();
                setUnsuccessful(false)
            }

        } catch (error) {
            console.log(error.response)
            setUnsuccessful(true)
            setSuccessful(false)
            if (error.response) {
                const { status } = error.response;

                if (status === 404) {
                    setErrorMessage("Goal under this name doesn't exist!")
                } else if (status === 400) {
                    setErrorMessage("Missing name or target, please provide us this information first!")
                }
            }
        }
    };


    return (<>
        <div className="container mx-auto my-8">
            <div className="h-12">
                <button onClick={openModal} name="AddButton" className="rounded bg-slate-600 text-white px-6 py-2 font-semibold">
                    Deposit!
                </button>
            </div>
        </div>
        <Transition appear show={isOpen} as={Fragment}>
            <Dialog
                as="div"
                className="fixed inset-0 z-10 overflow-y-auto"
                onClose={closeModal}>
                <div className="min-h-screen px-4 text-center">
                    <Transition.Child as={Fragment}
                                      enter="ease-out duration-300"
                                      enterFrom="opacity-0 scale-95"
                                      enterTo="opacity-100 scale-100"
                                      leave="ease-in duration-200"
                                      leaveFrom="opacity-100 scale-100"
                                      leaveTo="opacity-0 scale-95">
                        <div
                            className="inline-block w-full max-w-md p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white shadow-xl rounded-md" >
                            <Dialog.Title
                                as="h3"
                                className="text-lg font-medium leading-6 text-gray-900">
                                Deposit money to your Financial Goal!
                            </Dialog.Title>
                            <div className="flex max-w-md max-auto">
                                <div className="py-2">
                                    <div className="h-14 my-4">
                                        <label className="block text-gray-600 text-sm font-normal" onKeyDown={e => {if (e.key === "Enter") {addGoal(e).catch(error)}}}>
                                            Name of Goal
                                            <input
                                                type="text"
                                                name="nameOfGoal"
                                                defaultValue={null}
                                                onChange={handleNameOfGoalChange}
                                                className="h-10 w-96 border mt-2 px-2 py-2"
                                                required>
                                            </input>
                                        </label>
                                    </div>
                                    <div className="h-14 my-4">
                                        <label className="block text-gray-600 text-sm font-normal" onKeyDown={e => {if (e.key === "Enter") {addGoal(e).catch(error)}}}>
                                            Deposit
                                            <input
                                                type="number"
                                                name="deposit"
                                                defaultValue={null}
                                                onChange={handleDepositChange}
                                                className="h-10 w-96 border mt-2 px-2 py-2"
                                                required>
                                            </input>
                                        </label>
                                    </div>
                                    {successful && <p className="text-green-600">{message}</p> }
                                    {unsuccessful && <p className="text-red-600">{errorMessage}</p>}
                                    <div className="h-14 my-4 space-x-4 pt-4">
                                        <button type="submit" onClick={addGoal}
                                                className="rounded bg-green-400 hover:bg-green-700 text-white px-6 py-2 font-semibold">
                                            Deposit!
                                        </button>
                                        <button onClick={closeModal}
                                                className="rounded bg-red-400 hover:bg-red-700 text-white px-6 py-2 font-semibold">
                                            Close!
                                        </button>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </Transition.Child>
                </div>
            </Dialog>

        </Transition>
    </>);
};

export default DepositGoalButton;