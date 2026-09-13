import "../App.css";

import Library from "../components/layout/Library";
import NavigationSidebar from "../components/layout/NavigationSidebar";
import Header from "../components/layout/Header";
import ListView from "../components/layout/ListView";
import NoteDetail from "../components/layout/NoteDetail";
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { getAllNotes, getToDos } from "../features/notes/notesService";

const DashboardPage = () => {
  const dispatch = useDispatch();
  useEffect(() => {
    dispatch(getToDos());
    dispatch(getAllNotes());
  }, [dispatch]);

  return (
    <>
      <Header />
      <section className="dashboard container m-auto">
        <main className="grid grid-cols-4 w-full ">
          <div className="">
            <Library />
          </div>
          <div className=" ">
            <NavigationSidebar />
          </div>
          <div className=" ">
            <ListView />
          </div>
          <div className=" ">
            <NoteDetail />
          </div>
        </main>
      </section>
    </>
  );
};

export default DashboardPage;
