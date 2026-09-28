import { useDispatch } from "react-redux";
import { logout } from "../../features/auth/authSlice";

const Sidebar = ({  hidden, setIsOpen }) => {
  const dispatch=useDispatch()
  return (
    // حاوية عامة تتحكم في ظهور الخلفية المعتمة
    <div
      className={`fixed inset-0 z-50 transition-opacity duration-300 ${hidden ? "pointer-events-none opacity-0" : "opacity-100"}`}
    >
      {/* خلفية معتمة تتلاشى مع الانميشن */}
      <div
        className="absolute inset-0 bg-black/20"
        onClick={() => setIsOpen(false)}
      ></div>

      {/* القائمة الجانبية مع انميشن الانزلاق من اليمين */}
      <div
        className={`fixed top-0 right-0 w-1/4 h-screen bg-amber-800 text-white shadow-2xl transition-transform duration-300 ease-in-out flex flex-col ${hidden ? "translate-x-full" : "translate-x-0"}`}
      >
        <button
          className="p-4  text-2xl self-end hover:bg-amber-700 transition-colors"
          onClick={() => setIsOpen(false)}
        >
          ✕
        </button>

        <div className="flex flex-col gap-2 p-4">
            <button
              className="block text-start w-full h-full p-4 text-xl hover:bg-amber-700 rounded-lg cursor-pointer transition-colors"
            onClick={()=>dispatch(logout())}
           >
              logout
            </button>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
