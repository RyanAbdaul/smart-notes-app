import brand from "../../assets/screen.png";
import { MdFormatListBulletedAdd } from "react-icons/md";
import { RxAvatar } from "react-icons/rx";
import { FaArrowDown } from "react-icons/fa";
import Sidebar from "../common/Sidebar";
import { useState } from "react";
import Search from "../common/Search";
import { useDispatch } from "react-redux";
import { setActiveNote } from "../../features/notes/notesSlice";

const Header = () => {
  const [isOpen, setIsOpen] = useState(false);
  const toggleMenu = () => {
    setIsOpen(!isOpen);
  };
const dispatch=useDispatch()
  return (
    <header className="px-5 py-3 m-auto flex items-center w-full bg-[#FCF8FB]">
      {/* brand logo */}
      <div className="flex items-center w-1/3">
        <span>
          <img src={brand} alt="" className="" />
        </span>
        <span className="font-bold text-gray-600 text-xl ">Smart Notes</span>
      </div>
      {/* search field */}
      <Search />
      {/* Add Task Buttom */}
      <div className="w-1/3">
        <button 
        onClick={()=>dispatch(setActiveNote(null))}
        className="bg-amber-600 p-3 flex justify-between items-center rounded-xl  text-amber-50 font-bold hover:cursor-pointer hover:bg-amber-700">
          <span className="text-2xl mr-1">
            <MdFormatListBulletedAdd />
          </span>
          New Note
        </button>
      </div>
      {/* Auth select menu*/}

      <button
        name=""
        id="login"
        className=" flex items-center outline-none hover:cursor-pointer"
        onClick={() => toggleMenu()}
      >
        <span className="text-5xl mr-1">
          <RxAvatar />
        </span>
        <FaArrowDown />
      </button>
      <Sidebar
        hidden={!isOpen}
        setIsOpen={setIsOpen}
      />
    </header>
  );
};

export default Header;
